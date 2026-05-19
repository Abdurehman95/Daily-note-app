package com.efoy.money.lab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efoy.money.lab.presentation.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    noteId: Int,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditNote: (Int) -> Unit
) {
    val notes by viewModel.notesListState.collectAsState()
    var lastNonNullNote by remember { mutableStateOf<com.efoy.money.lab.data.local.NoteEntity?>(null) }
    val note = remember(notes, noteId) {
        notes.find { it.id == noteId }?.also { lastNonNullNote = it } ?: lastNonNullNote
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (note == null) {
        // Safe check if note gets deleted or is not found
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    val noteColor = remember(note.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (e: Exception) {
            Color(0xFFFFF5E6)
        }
    }

    val formattedDate = remember(note.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(note.timestamp))
    }

    val wordCount = remember(note.content) {
        if (note.content.isBlank()) 0 else note.content.trim().split("\\s+".toRegex()).size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Read Note", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadNoteForEditing(note.id)
                        onNavigateToEditNote(note.id)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = noteColor
                )
            )
        },
        containerColor = noteColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Category tag and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = note.category,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$wordCount words",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = note.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.9f),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = note.content,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.75f),
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note?") },
            text = { Text("This note will be permanently removed from your storage.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteNote(note) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
