package com.efoy.money.lab.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.efoy.money.lab.presentation.viewmodel.AppViewModel
import com.efoy.money.lab.ui.AddEditNoteScreen
import com.efoy.money.lab.ui.DashboardScreen
import com.efoy.money.lab.ui.NoteDetailsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        // 1. Dashboard Screen (Home)
        composable(route = "dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddNote = {
                    navController.navigate("add_edit_note")
                },
                onNavigateToNoteDetails = { noteId ->
                    navController.navigate("note_details/$noteId")
                }
            )
        }

        // 2. Note Details Screen
        composable(
            route = "note_details/{noteId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteDetailsScreen(
                noteId = noteId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditNote = { id ->
                    navController.navigate("add_edit_note?noteId=$id") {
                        // Pop details from stack when editing to prevent cycle loops
                        popUpTo("dashboard")
                    }
                }
            )
        }

        // 3. Add/Edit Note Screen
        composable(
            route = "add_edit_note?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            
            // Trigger pre-filling only if editing
            LaunchedEffect(noteId) {
                if (noteId != -1) {
                    viewModel.loadNoteForEditing(noteId)
                } else {
                    viewModel.clearEditor()
                }
            }

            AddEditNoteScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
