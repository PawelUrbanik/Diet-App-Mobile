package pl.pawel.diet_app_mobile.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Wspólny dialog potwierdzenia usunięcia elementu (np. po przesunięciu w lewo).
 */
@Composable
fun ConfirmDeleteDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Usunąć?",
    confirmLabel: String = "Usuń",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}
