package pl.pawel.diet_app_mobile.ui.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import pl.pawel.diet_app_mobile.data.preferences.ShoppingRangeDates
import pl.pawel.diet_app_mobile.domain.model.ShoppingListItem
import pl.pawel.diet_app_mobile.ui.components.ConfirmDeleteDialog
import pl.pawel.diet_app_mobile.ui.components.SwipeToDeleteContainer

private val POLISH_LOCALE: Locale = Locale.forLanguageTag("pl")
private val RANGE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM", POLISH_LOCALE)
private val CUSTOM_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", POLISH_LOCALE)

@Composable
fun ShoppingRoute(
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    val groups by viewModel.groups.collectAsState()
    val generateDialog by viewModel.generateDialog.collectAsState()
    val manualDialog by viewModel.manualDialog.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val message by viewModel.message.collectAsState()
    val currentRange by viewModel.currentRange.collectAsState()

    ShoppingScreen(
        groups = groups,
        currentRange = currentRange,
        generateDialog = generateDialog,
        manualDialog = manualDialog,
        isGenerating = isGenerating,
        message = message,
        onConsumeMessage = viewModel::consumeMessage,
        onOpenGenerate = viewModel::openGenerateDialog,
        onCloseGenerate = viewModel::closeGenerateDialog,
        onRangeChange = viewModel::onRangeChange,
        onCustomStartChange = viewModel::onCustomStartChange,
        onCustomEndChange = viewModel::onCustomEndChange,
        onToggleExcludedMeal = viewModel::onToggleExcludedMeal,
        onConfirmGenerate = viewModel::confirmGenerate,
        onToggleChecked = viewModel::toggleChecked,
        onRemoveItem = viewModel::removeItem,
        onOpenManual = viewModel::openManualDialog,
        onCloseManual = viewModel::closeManualDialog,
        onManualNameChange = viewModel::onManualNameChange,
        onConfirmManual = viewModel::confirmManual,
        onClearChecked = viewModel::clearChecked,
        onClearAll = viewModel::clearAll,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingScreen(
    groups: List<ShoppingGroup>,
    currentRange: ShoppingRangeDates?,
    generateDialog: GenerateDialogState?,
    manualDialog: ManualDialogState?,
    isGenerating: Boolean,
    message: String?,
    onConsumeMessage: () -> Unit,
    onOpenGenerate: () -> Unit,
    onCloseGenerate: () -> Unit,
    onRangeChange: (ShoppingRange) -> Unit,
    onCustomStartChange: (LocalDate) -> Unit,
    onCustomEndChange: (LocalDate) -> Unit,
    onToggleExcludedMeal: (Long) -> Unit,
    onConfirmGenerate: () -> Unit,
    onToggleChecked: (ShoppingListItem) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onOpenManual: () -> Unit,
    onCloseManual: () -> Unit,
    onManualNameChange: (String) -> Unit,
    onConfirmManual: () -> Unit,
    onClearChecked: () -> Unit,
    onClearAll: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }
    var itemPendingDelete by remember { mutableStateOf<ShoppingListItem?>(null) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            onConsumeMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Zakupy")
                        if (currentRange != null) {
                            Text(
                                text = formatRange(currentRange.start, currentRange.end),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenGenerate) {
                        Icon(Icons.Default.Refresh, contentDescription = "Generuj z planu")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Więcej")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Wyczyść kupione") },
                            onClick = {
                                menuExpanded = false
                                onClearChecked()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Wyczyść wszystko") },
                            onClick = {
                                menuExpanded = false
                                onClearAll()
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenManual,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Dodaj") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isGenerating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (groups.isEmpty()) {
                EmptyShoppingState(onGenerate = onOpenGenerate)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    groups.forEach { group ->
                        item(key = "header-${group.category}") {
                            CategoryHeader(
                                category = group.category,
                                doneCount = group.items.count { it.isChecked },
                                total = group.items.size,
                            )
                        }
                        items(items = group.items, key = { it.id }) { item ->
                            SwipeToDeleteContainer(onDeleteRequest = { itemPendingDelete = item }) {
                                ShoppingItemRow(
                                    item = item,
                                    onToggle = { onToggleChecked(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (generateDialog != null) {
        GenerateDialog(
            state = generateDialog,
            onRangeChange = onRangeChange,
            onCustomStartChange = onCustomStartChange,
            onCustomEndChange = onCustomEndChange,
            onToggleMeal = onToggleExcludedMeal,
            onConfirm = onConfirmGenerate,
            onDismiss = onCloseGenerate,
        )
    }

    if (manualDialog != null) {
        ManualItemDialog(
            state = manualDialog,
            onNameChange = onManualNameChange,
            onConfirm = onConfirmManual,
            onDismiss = onCloseManual,
        )
    }

    itemPendingDelete?.let { item ->
        ConfirmDeleteDialog(
            text = "Usunąć ${item.name} z listy zakupów?",
            onConfirm = {
                onRemoveItem(item.id)
                itemPendingDelete = null
            },
            onDismiss = { itemPendingDelete = null },
        )
    }
}

@Composable
private fun CategoryHeader(category: String, doneCount: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "$doneCount/$total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ShoppingItemRow(item: ShoppingListItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = { onToggle() })
        val decoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
        val contentColor = if (item.isChecked) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onSurface
        }
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = decoration,
            color = contentColor,
            modifier = Modifier.weight(1f),
        )
        if (item.quantityGrams > 0.0) {
            Text(
                text = formatQuantity(item.quantityGrams),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = decoration,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyShoppingState(onGenerate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Lista zakupów jest pusta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Wygeneruj listę na podstawie zaplanowanych posiłków lub dodaj produkty ręcznie.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onGenerate) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(" Generuj z planu")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateDialog(
    state: GenerateDialogState,
    onRangeChange: (ShoppingRange) -> Unit,
    onCustomStartChange: (LocalDate) -> Unit,
    onCustomEndChange: (LocalDate) -> Unit,
    onToggleMeal: (Long) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val today = remember { LocalDate.now() }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generuj z planu") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Wybierz zakres dni:",
                    style = MaterialTheme.typography.bodyMedium,
                )
                ShoppingRange.entries.forEach { range ->
                    val subtitle = if (range == ShoppingRange.Custom) {
                        formatRange(state.customStart, state.customEnd)
                    } else {
                        val (start, end) = range.resolve(today)
                        formatRange(start, end)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = state.selectedRange == range,
                                onClick = { onRangeChange(range) },
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RadioButton(
                            selected = state.selectedRange == range,
                            onClick = { onRangeChange(range) },
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(range.label, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (state.selectedRange == ShoppingRange.Custom) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { showStartPicker = true },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Od: ${state.customStart.format(CUSTOM_DATE_FORMATTER)}")
                        }
                        OutlinedButton(
                            onClick = { showEndPicker = true },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Do: ${state.customEnd.format(CUSTOM_DATE_FORMATTER)}")
                        }
                    }
                }

                if (state.meals.isNotEmpty()) {
                    Text(
                        text = "Pomiń posiłki (odznacz, by nie dodawać składników):",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    state.meals.forEach { meal ->
                        val included = meal.mealId !in state.excludedMealIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleMeal(meal.mealId) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Checkbox(
                                checked = included,
                                onCheckedChange = { onToggleMeal(meal.mealId) },
                            )
                            Text(
                                text = if (meal.occurrences > 1) {
                                    "${meal.name} ×${meal.occurrences}"
                                } else {
                                    meal.name
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Generuj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )

    if (showStartPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.customStart.toUtcMillis(),
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onCustomStartChange(it.utcMillisToLocalDate()) }
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Anuluj") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showEndPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.customEnd.toUtcMillis(),
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onCustomEndChange(it.utcMillisToLocalDate()) }
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Anuluj") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun ManualItemDialog(
    state: ManualDialogState,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj produkt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Nazwa") },
                    singleLine = true,
                    isError = state.errorMessage != null,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Dodaj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

private fun formatRange(start: LocalDate, end: LocalDate): String =
    if (start == end) {
        start.format(RANGE_FORMATTER)
    } else {
        "${start.format(RANGE_FORMATTER)} – ${end.format(RANGE_FORMATTER)}"
    }

private fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.utcMillisToLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

private fun formatQuantity(grams: Double): String =
    if (grams >= 1000.0) {
        "%.2f kg".format(POLISH_LOCALE, grams / 1000.0)
    } else {
        "${grams.toInt()} g"
    }
