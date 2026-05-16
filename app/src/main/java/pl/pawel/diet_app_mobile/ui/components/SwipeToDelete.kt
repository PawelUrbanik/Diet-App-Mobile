package pl.pawel.diet_app_mobile.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequest()
            }
            false // card zawsze wraca — dialog obsługuje usunięcie
        },
    )

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val targetColor = if (state.targetValue == SwipeToDismissBoxValue.EndToStart) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                Color.Transparent
            }
            val bgColor by animateColorAsState(
                targetValue = targetColor,
                label = "swipe_bg",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (state.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Usuń",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        },
        content = { content() },
    )
}
