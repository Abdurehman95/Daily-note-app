package com.efoy.money.lab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efoy.money.lab.presentation.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // 5 Exquisite modern pastel notes color schemes
    val colorPalette = listOf(
        Pair("#FFF5E6", "Peach"),
        Pair("#E8DFF5", "Lavender"),
        Pair("#E2F0D9", "Mint"),
        Pair("#D5EEFF", "Soft Blue"),
        Pair("#ECEFF1", "Slate Gray")
    )

    val categories = listOf("Work", "Personal", "Ideas", "Todo")

    val currentBgColor = remember(state.noteColorHexInput) {
        try {
            Color(android.graphics.Color.parseColor(state.noteColorHexInput))
        } catch (e: Exception) {
            Color(0xFFFFF5E6)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (state.editingNoteId == null) "Create Note" else "Edit Note",
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearEditor()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveNote {
                                onNavigateBack()
                            }
                        },
                        enabled = state.noteTitleInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentBgColor
                )
            )
        },
        containerColor = currentBgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Color Selector Header
            Text(
                text = "SELECT COLOR THEME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colorPalette.forEach { pair ->
                    val colorHex = pair.first
                    val isSelected = state.noteColorHexInput == colorHex
                    val parsedColor = Color(android.graphics.Color.parseColor(colorHex))
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                            .clickable { viewModel.onColorChange(colorHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Selector
            Text(
                text = "CATEGORY TAG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    val isSelected = state.noteCategoryInput == cat
                    InputChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategoryChange(cat) },
                        label = { Text(cat) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color.Black.copy(alpha = 0.12f),
                            selectedLabelColor = Color.Black,
                            labelColor = Color.Black.copy(alpha = 0.6f)
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            borderColor = if (isSelected) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.1f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Inputs
            OutlinedTextField(
                value = state.noteTitleInput,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = { Text("Note Title", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.85f)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.noteContentInput,
                onValueChange = { viewModel.onContentChange(it) },
                placeholder = { Text("Write something inspirational here...", color = Color.Black.copy(alpha = 0.4f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.Black.copy(alpha = 0.75f)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
