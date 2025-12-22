package com.safemail.safemailapp.uiLayer.hubTask.stickyNotes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteModel


import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickyNotesScreen(
    viewModel: StickyNotesViewModel = viewModel(),
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sticky Notes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF9C4),
                    titleContentColor = Color(0xFF5D4037)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFD54F),
                contentColor = Color(0xFF5D4037),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (notes.isEmpty()) {
                EmptyState()
            } else {
                StickyNotesGrid(
                    notes = notes,
                    onNoteLongPress = { viewModel.deleteNote(it) },
                    onNoteUpdate = { id, text -> viewModel.updateNote(id, text) }
                )
            }
        }

        if (showAddDialog) {
            AddNoteDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { text, color ->
                    viewModel.addNote(text, color)
                    showAddDialog = false
                }
            )
        }
    }
}

/* -------------------- COMPONENTS -------------------- */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickyNotesGrid(
    notes: List<StickyNoteModel>,
    onNoteLongPress: (Int) -> Unit,
    onNoteUpdate: (Int, String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(notes, key = { it.id }) { note ->
            EditableStickyNote(
                note = note,
                onLongPress = { onNoteLongPress(note.id) },
                onTextChange = { onNoteUpdate(note.id, it) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditableStickyNote(
    note: StickyNoteModel,
    onLongPress: () -> Unit,
    onTextChange: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(note.text) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { isEditing = !isEditing },
                onLongClick = onLongPress
            )
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(note.color))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                TextField(
                    value = text,
                    onValueChange = { text = it; onTextChange(it) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            } else {
                Text(text = text, fontSize = 15.sp, color = Color(0xFF3E2723), maxLines = 10)
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onAdd: (String, Long) -> Unit) {
    var text by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFFFFF59D) }
    val colors = listOf(0xFFFFF59D, 0xFFFFCCBC, 0xFFC5E1A5, 0xFFB3E5FC, 0xFFF8BBD0, 0xFFD1C4E9)

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(selectedColor))) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("New Note", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Write something...") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    colors.forEach { color ->
                        ColorOption(Color(color), selectedColor == color) { selectedColor = color }
                    }
                }
                Button(
                    onClick = { onAdd(text, selectedColor) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
                ) { Text("Add Note") }
            }
        }
    }
}

@Composable
fun ColorOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(36.dp).clickable { onClick() },
        shape = CircleShape,
        color = color,
        border = if (isSelected) BorderStroke(3.dp, Color(0xFF5D4037)) else null
    ) {}
}

@Composable
fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("📝 No notes yet. Tap + to start!", color = Color.Gray)
    }
}