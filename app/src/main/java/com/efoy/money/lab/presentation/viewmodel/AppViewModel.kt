package com.efoy.money.lab.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.efoy.money.lab.data.local.NoteDatabase
import com.efoy.money.lab.data.local.NoteEntity
import com.efoy.money.lab.data.network.QuoteApiService
import com.efoy.money.lab.utils.TextToSpeechHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppState(
    val notes: List<NoteEntity> = emptyList(),
    val searchQuery: String = "",
    val activeCategory: String = "All",
    val fetchedQuote: String = "Act as if what you do makes a difference. It does.",
    val fetchedAuthor: String = "William James",
    val isFetching: Boolean = false,
    val noteTitleInput: String = "",
    val noteContentInput: String = "",
    val noteCategoryInput: String = "Personal",
    val noteColorHexInput: String = "#FFF5E6", // Default soft orange
    val editingNoteId: Int? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val dao = db.noteDao()
    private val apiService = QuoteApiService()
    private val ttsHelper = TextToSpeechHelper(application)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    // 10 Curated robust offline backup quotes for failsafe presentation
    private val fallbackQuotes = listOf(
        Pair("Discipline is the bridge between goals and accomplishment.", "Jim Rohn"),
        Pair("It always seems impossible until it's done.", "Nelson Mandela"),
        Pair("Quality is not an act, it is a habit.", "Aristotle"),
        Pair("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        Pair("Do what you can, with what you have, where you are.", "Theodore Roosevelt"),
        Pair("The only way to do great work is to love what you do.", "Steve Jobs"),
        Pair("Believe you can and you're halfway there.", "Theodore Roosevelt"),
        Pair("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        Pair("Perseverance is not a long race; it is many short races one after the other.", "Walter Elliot"),
        Pair("Your talent determines what you can do. Your motivation determines how much you are willing to do.", "Lou Holtz")
    )

    // Flow for real-time notes combined with searches & categories
    @OptIn(ExperimentalCoroutinesApi::class)
    val notesListState: StateFlow<List<NoteEntity>> = _state
        .flatMapLatest { state ->
            dao.searchNotes(state.searchQuery)
        }
        .combine(_state) { list, state ->
            if (state.activeCategory == "All") {
                list
            } else {
                list.filter { it.category.equals(state.activeCategory, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Automatically fetch quote from the API on initialization
        fetchQuote()
    }

    fun fetchQuote() {
        viewModelScope.launch {
            _state.update { it.copy(isFetching = true) }
            val result = apiService.fetchRandomQuote()
            result.onSuccess { pair ->
                _state.update {
                    it.copy(
                        fetchedQuote = pair.first,
                        fetchedAuthor = pair.second,
                        isFetching = false
                    )
                }
            }.onFailure {
                // Fallback gracefully to a random high-quality local cache quote
                val localQuote = fallbackQuotes.random()
                _state.update {
                    it.copy(
                        fetchedQuote = localQuote.first,
                        fetchedAuthor = localQuote.second,
                        isFetching = false
                    )
                }
            }
        }
    }

    // Speech controls
    fun speakQuote() {
        val currentState = _state.value
        val textToSpeak = "\"${currentState.fetchedQuote}\" by ${currentState.fetchedAuthor}"
        ttsHelper.speak(textToSpeak)
    }

    fun stopTts() {
        ttsHelper.stop()
    }

    // Notes inputs modifiers
    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(noteTitleInput = newTitle) }
    }

    fun onContentChange(newContent: String) {
        _state.update { it.copy(noteContentInput = newContent) }
    }

    fun onCategoryChange(newCategory: String) {
        _state.update { it.copy(noteCategoryInput = newCategory) }
    }

    fun onColorChange(newColorHex: String) {
        _state.update { it.copy(noteColorHexInput = newColorHex) }
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setActiveCategory(category: String) {
        _state.update { it.copy(activeCategory = category) }
    }

    // CRUD notes actions
    fun loadNoteForEditing(noteId: Int) {
        viewModelScope.launch {
            val note = dao.getNoteById(noteId)
            if (note != null) {
                _state.update {
                    it.copy(
                        editingNoteId = note.id,
                        noteTitleInput = note.title,
                        noteContentInput = note.content,
                        noteCategoryInput = note.category,
                        noteColorHexInput = note.colorHex
                    )
                }
            }
        }
    }

    fun clearEditor() {
        _state.update {
            it.copy(
                editingNoteId = null,
                noteTitleInput = "",
                noteContentInput = "",
                noteCategoryInput = "Personal",
                noteColorHexInput = "#FFF5E6"
            )
        }
    }

    fun saveNote(onComplete: () -> Unit) {
        val currentState = _state.value
        if (currentState.noteTitleInput.isBlank()) return // Simple validation

        viewModelScope.launch {
            val note = NoteEntity(
                id = currentState.editingNoteId ?: 0,
                title = currentState.noteTitleInput,
                content = currentState.noteContentInput,
                timestamp = System.currentTimeMillis(),
                category = currentState.noteCategoryInput,
                colorHex = currentState.noteColorHexInput
            )
            dao.insertNote(note)
            clearEditor()
            onComplete()
        }
    }

    fun deleteNote(note: NoteEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dao.deleteNote(note)
            onComplete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
