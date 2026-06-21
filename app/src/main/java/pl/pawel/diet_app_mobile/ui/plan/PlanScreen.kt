package pl.pawel.diet_app_mobile.ui.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.LocalDate
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.time.format.DateTimeFormatter
import java.util.Locale
import pl.pawel.diet_app_mobile.domain.model.DayPlan
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.domain.model.WeekTemplate
import pl.pawel.diet_app_mobile.ui.components.AppSearchBar
import pl.pawel.diet_app_mobile.ui.components.ConfirmDeleteDialog
import pl.pawel.diet_app_mobile.ui.components.QrCodeImage
import pl.pawel.diet_app_mobile.ui.components.NutritionMacroBars
import pl.pawel.diet_app_mobile.ui.components.SwipeToDeleteContainer
import pl.pawel.diet_app_mobile.ui.components.mealCategoryIcon
import pl.pawel.diet_app_mobile.ui.theme.MealColorDrugieSniadanie
import pl.pawel.diet_app_mobile.ui.theme.MealColorKolacja
import pl.pawel.diet_app_mobile.ui.theme.MealColorObiad
import pl.pawel.diet_app_mobile.ui.theme.MealColorPodwieczorek
import pl.pawel.diet_app_mobile.ui.theme.MealColorSniadanie

private val POLISH_LOCALE: Locale = Locale.forLanguageTag("pl")
private val DAY_LABEL_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE", POLISH_LOCALE)
private val DAY_NUMBER_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d", POLISH_LOCALE)
private val FULL_DAY_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMMM", POLISH_LOCALE)
private val WEEK_RANGE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM", POLISH_LOCALE)

@Composable
fun PlanRoute(
    viewModel: PlanViewModel = hiltViewModel(),
) {
    val plan by viewModel.plan.collectAsState()
    val weekStartDate by viewModel.weekStartDate.collectAsState()
    val addSheet by viewModel.addSheet.collectAsState()
    val servingsDialog by viewModel.servingsDialog.collectAsState()
    val editDialog by viewModel.editDialog.collectAsState()
    val availableMeals by viewModel.availableMeals.collectAsState()
    val templates by viewModel.templates.collectAsState()
    val templatesSheet by viewModel.templatesSheet.collectAsState()
    val saveTemplateDialog by viewModel.saveTemplateDialog.collectAsState()
    val applyConfirm by viewModel.applyConfirm.collectAsState()
    val shareQr by viewModel.shareQr.collectAsState()
    val importPlan by viewModel.importPlan.collectAsState()
    val message by viewModel.message.collectAsState()

    PlanScreen(
        plan = plan,
        weekStartDate = weekStartDate,
        addSheet = addSheet,
        servingsDialog = servingsDialog,
        editDialog = editDialog,
        availableMeals = availableMeals,
        templates = templates,
        templatesSheet = templatesSheet,
        saveTemplateDialog = saveTemplateDialog,
        applyConfirm = applyConfirm,
        shareQr = shareQr,
        importPlan = importPlan,
        message = message,
        onConsumeMessage = viewModel::consumeMessage,
        onOpenShareQr = viewModel::openShareQr,
        onCloseShareQr = viewModel::closeShareQr,
        onPlanScanned = viewModel::onPlanScanned,
        onImportWeekChange = viewModel::onImportWeekChange,
        onConfirmImport = viewModel::confirmImport,
        onCancelImport = viewModel::cancelImport,
        onOpenApplyTemplates = viewModel::openApplyTemplatesSheet,
        onCloseTemplatesSheet = viewModel::closeTemplatesSheet,
        onSelectTemplate = viewModel::requestApplyTemplate,
        onConfirmApply = viewModel::confirmApplyTemplate,
        onCancelApply = viewModel::cancelApplyTemplate,
        onDeleteTemplate = viewModel::deleteTemplate,
        onOpenSaveTemplate = viewModel::openSaveTemplateDialog,
        onCloseSaveTemplate = viewModel::closeSaveTemplateDialog,
        onSaveTemplateNameChange = viewModel::onSaveTemplateNameChange,
        onConfirmSaveTemplate = viewModel::confirmSaveTemplate,
        onPreviousWeek = viewModel::goToPreviousWeek,
        onNextWeek = viewModel::goToNextWeek,
        onToday = viewModel::goToCurrentWeek,
        onOpenAddSheet = viewModel::openAddSheet,
        onCloseAddSheet = viewModel::closeAddSheet,
        onAddQueryChange = viewModel::onAddQueryChange,
        onAddMealTypeChange = viewModel::onAddMealTypeChange,
        onShowAllCategoriesChange = viewModel::onShowAllCategoriesChange,
        onSelectMeal = viewModel::onSelectMeal,
        onCloseServingsDialog = viewModel::closeServingsDialog,
        onDialogServingsChange = viewModel::onDialogServingsChange,
        onConfirmAdd = viewModel::confirmAdd,
        onOpenEditDialog = viewModel::openEditDialog,
        onCloseEditDialog = viewModel::closeEditDialog,
        onEditServingsChange = viewModel::onEditServingsChange,
        onConfirmEdit = viewModel::confirmEdit,
        onRemoveFromEdit = viewModel::removePlannedMealFromEdit,
        onSwapFromEdit = viewModel::openSwapFromEdit,
        onSwipeRemove = viewModel::removePlannedMeal,
        onCopyFromPreviousDay = viewModel::copyFromPreviousDay,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanScreen(
    plan: MealPlan,
    weekStartDate: LocalDate,
    addSheet: AddMealSheetState?,
    servingsDialog: ServingsDialogState?,
    editDialog: EditServingsDialogState?,
    availableMeals: List<Meal>,
    templates: List<WeekTemplate>,
    templatesSheet: TemplatesSheetMode?,
    saveTemplateDialog: SaveTemplateDialogState?,
    applyConfirm: ApplyTemplateConfirmState?,
    shareQr: ShareQrState?,
    importPlan: ImportPlanState?,
    message: String?,
    onConsumeMessage: () -> Unit,
    onOpenShareQr: () -> Unit,
    onCloseShareQr: () -> Unit,
    onPlanScanned: (String) -> Unit,
    onImportWeekChange: (LocalDate) -> Unit,
    onConfirmImport: () -> Unit,
    onCancelImport: () -> Unit,
    onOpenApplyTemplates: () -> Unit,
    onCloseTemplatesSheet: () -> Unit,
    onSelectTemplate: (WeekTemplate) -> Unit,
    onConfirmApply: () -> Unit,
    onCancelApply: () -> Unit,
    onDeleteTemplate: (WeekTemplate) -> Unit,
    onOpenSaveTemplate: () -> Unit,
    onCloseSaveTemplate: () -> Unit,
    onSaveTemplateNameChange: (String) -> Unit,
    onConfirmSaveTemplate: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToday: () -> Unit,
    onOpenAddSheet: (LocalDate, String) -> Unit,
    onCloseAddSheet: () -> Unit,
    onAddQueryChange: (String) -> Unit,
    onAddMealTypeChange: (String) -> Unit,
    onShowAllCategoriesChange: (Boolean) -> Unit,
    onSelectMeal: (Meal) -> Unit,
    onCloseServingsDialog: () -> Unit,
    onDialogServingsChange: (Double) -> Unit,
    onConfirmAdd: () -> Unit,
    onOpenEditDialog: (PlannedMeal) -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditServingsChange: (Double) -> Unit,
    onConfirmEdit: () -> Unit,
    onRemoveFromEdit: () -> Unit,
    onSwapFromEdit: () -> Unit,
    onSwipeRemove: (Long) -> Unit,
    onCopyFromPreviousDay: (LocalDate, String) -> Unit,
) {
    val today = remember { LocalDate.now() }
    val initialPage = if (today in weekStartDate..weekStartDate.plusDays(6)) {
        today.dayOfWeek.value - 1
    } else {
        0
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 7 })
    val snackbarHostState = remember { SnackbarHostState() }
    var overflowExpanded by remember { mutableStateOf(false) }
    var plannedMealPendingDelete by remember { mutableStateOf<PlannedMeal?>(null) }
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let(onPlanScanned)
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            onConsumeMessage()
        }
    }

    LaunchedEffect(weekStartDate) {
        val newPage = if (today in weekStartDate..weekStartDate.plusDays(6)) {
            today.dayOfWeek.value - 1
        } else {
            0
        }
        pagerState.scrollToPage(newPage)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Plan") },
                actions = {
                    IconButton(onClick = onToday) {
                        Icon(Icons.Default.Today, contentDescription = "Bieżący tydzień")
                    }
                    IconButton(onClick = { overflowExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Więcej")
                    }
                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Zastosuj szablon") },
                            onClick = {
                                overflowExpanded = false
                                onOpenApplyTemplates()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Zapisz tydzień jako szablon") },
                            enabled = plan.hasPlannedMeals,
                            onClick = {
                                overflowExpanded = false
                                onOpenSaveTemplate()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Pokaż kod QR tygodnia") },
                            enabled = plan.hasPlannedMeals,
                            onClick = {
                                overflowExpanded = false
                                onOpenShareQr()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Skanuj kod QR") },
                            onClick = {
                                overflowExpanded = false
                                scanLauncher.launch(
                                    ScanOptions()
                                        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                        .setBeepEnabled(false)
                                        .setOrientationLocked(false)
                                        .setPrompt("Zeskanuj kod planu"),
                                )
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            WeekNavigator(
                weekStartDate = weekStartDate,
                onPrevious = onPreviousWeek,
                onNext = onNextWeek,
            )
            if (plan.hasPlannedMeals) {
                WeekTotalsRow(nutrition = plan.nutrition)
            }
            DayTabsRow(
                weekStartDate = weekStartDate,
                today = today,
                selectedIndex = pagerState.currentPage,
                onDayClick = { index ->
                    // Scroll handled via LaunchedEffect below or we'd need a coroutine here
                },
                pagerState = pagerState,
            )
            HorizontalDivider()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val dayPlan = plan.days.getOrNull(page)
                    ?: DayPlan(weekStartDate.plusDays(page.toLong()), emptyList())
                DayContent(
                    dayPlan = dayPlan,
                    isToday = dayPlan.date == today,
                    onOpenAddSheet = onOpenAddSheet,
                    onOpenEditDialog = onOpenEditDialog,
                    onRequestRemove = { plannedMealPendingDelete = it },
                    onCopyFromPreviousDay = onCopyFromPreviousDay,
                )
            }
        }
    }

    if (addSheet != null) {
        AddMealBottomSheet(
            state = addSheet,
            availableMeals = availableMeals,
            onDismiss = onCloseAddSheet,
            onQueryChange = onAddQueryChange,
            onMealTypeChange = onAddMealTypeChange,
            onShowAllCategoriesChange = onShowAllCategoriesChange,
            onSelectMeal = onSelectMeal,
        )
    }

    if (servingsDialog != null) {
        ServingsDialog(
            state = servingsDialog,
            onDismiss = onCloseServingsDialog,
            onServingsChange = onDialogServingsChange,
            onConfirm = onConfirmAdd,
        )
    }

    if (editDialog != null) {
        EditServingsDialog(
            state = editDialog,
            onDismiss = onCloseEditDialog,
            onServingsChange = onEditServingsChange,
            onConfirm = onConfirmEdit,
            onRemove = onRemoveFromEdit,
            onSwap = onSwapFromEdit,
        )
    }

    if (templatesSheet == TemplatesSheetMode.Apply) {
        TemplatesBottomSheet(
            templates = templates,
            onDismiss = onCloseTemplatesSheet,
            onSelect = onSelectTemplate,
            onDelete = onDeleteTemplate,
        )
    }

    if (applyConfirm != null) {
        ApplyTemplateConfirmDialog(
            state = applyConfirm,
            onConfirm = onConfirmApply,
            onDismiss = onCancelApply,
        )
    }

    if (saveTemplateDialog != null) {
        SaveTemplateDialog(
            state = saveTemplateDialog,
            onNameChange = onSaveTemplateNameChange,
            onConfirm = onConfirmSaveTemplate,
            onDismiss = onCloseSaveTemplate,
        )
    }

    plannedMealPendingDelete?.let { plannedMeal ->
        ConfirmDeleteDialog(
            text = "Usunąć ${plannedMeal.meal.name} z planu?",
            onConfirm = {
                onSwipeRemove(plannedMeal.id)
                plannedMealPendingDelete = null
            },
            onDismiss = { plannedMealPendingDelete = null },
        )
    }

    if (shareQr != null) {
        ShareQrDialog(state = shareQr, onDismiss = onCloseShareQr)
    }

    if (importPlan != null) {
        ImportPlanDialog(
            state = importPlan,
            onWeekChange = onImportWeekChange,
            onConfirm = onConfirmImport,
            onDismiss = onCancelImport,
        )
    }
}

@Composable
private fun ShareQrDialog(state: ShareQrState, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kod QR planu") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "${state.label} · ${state.mealCount} posiłków",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                QrCodeImage(content = state.payload, modifier = Modifier.size(260.dp))
                Text(
                    text = "Na drugim telefonie: Plan → Skanuj kod QR.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Zamknij") }
        },
    )
}

@Composable
private fun ImportPlanDialog(
    state: ImportPlanState,
    onWeekChange: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val weekEnd = state.targetWeekStart.plusDays(6)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zaimportować plan?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Zeskanowany plan: ${state.share.slots.size} posiłków.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Wybierz tydzień docelowy:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = { onWeekChange(state.targetWeekStart.minusWeeks(1)) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Poprzedni tydzień")
                    }
                    Text(
                        text = "${state.targetWeekStart.format(WEEK_RANGE_FORMATTER)}–" +
                            weekEnd.format(WEEK_RANGE_FORMATTER),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    IconButton(onClick = { onWeekChange(state.targetWeekStart.plusWeeks(1)) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Następny tydzień")
                    }
                }
                Text(
                    text = "Istniejące posiłki w tym tygodniu zostaną zastąpione.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Zastosuj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@Composable
private fun WeekNavigator(
    weekStartDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val weekEnd = weekStartDate.plusDays(6)
    val label = "${weekStartDate.format(WEEK_RANGE_FORMATTER)} – ${weekEnd.format(WEEK_RANGE_FORMATTER)}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Poprzedni tydzień")
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Następny tydzień")
        }
    }
}

@Composable
private fun WeekTotalsRow(nutrition: pl.pawel.diet_app_mobile.domain.model.NutritionSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Σ tydzień",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${nutrition.calories.formatKcal()} kcal",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "B ${nutrition.protein.formatKcal()}g · T ${nutrition.fat.formatKcal()}g · W ${nutrition.carbs.formatKcal()}g",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplatesBottomSheet(
    templates: List<WeekTemplate>,
    onDismiss: () -> Unit,
    onSelect: (WeekTemplate) -> Unit,
    onDelete: (WeekTemplate) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Zastosuj szablon tygodnia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (templates.isEmpty()) {
                Text(
                    text = "Brak szablonów. Zapisz najpierw tydzień jako szablon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Card {
                    templates.forEachIndexed { index, template ->
                        TemplateRow(
                            template = template,
                            onClick = { onSelect(template) },
                            onDelete = { onDelete(template) },
                        )
                        if (index < templates.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateRow(
    template: WeekTemplate,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(template.name) },
        supportingContent = {
            val tag = if (template.isPredefined) "Wbudowany" else "Twój"
            Text(
                text = "$tag · ${template.totalSlots} posiłków",
                style = MaterialTheme.typography.bodySmall,
            )
        },
        trailingContent = {
            if (!template.isPredefined) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Usuń szablon",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun ApplyTemplateConfirmDialog(
    state: ApplyTemplateConfirmState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zastosować szablon?") },
        text = {
            val replaceLine = if (state.existingMealsCount > 0) {
                "Bieżący tydzień zawiera ${state.existingMealsCount} posiłków — zostaną zastąpione."
            } else {
                "Bieżący tydzień jest pusty."
            }
            Text("Szablon „${state.template.name}” doda ${state.template.totalSlots} posiłków.\n\n$replaceLine")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Zastosuj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@Composable
private fun SaveTemplateDialog(
    state: SaveTemplateDialogState,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zapisz tydzień jako szablon") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Nazwa szablonu") },
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
            TextButton(onClick = onConfirm) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayTabsRow(
    weekStartDate: LocalDate,
    today: LocalDate,
    selectedIndex: Int,
    onDayClick: (Int) -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        (0..6).forEach { offset ->
            val date = weekStartDate.plusDays(offset.toLong())
            val isSelected = offset == selectedIndex
            val isToday = date == today
            val bgColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isToday -> MaterialTheme.colorScheme.primaryContainer
                else -> Color.Transparent
            }
            val contentColor = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            }
            Column(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable {
                        scope.launch { pagerState.animateScrollToPage(offset) }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = date.format(DAY_LABEL_FORMATTER).take(2),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                )
                Text(
                    text = date.format(DAY_NUMBER_FORMATTER),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun DayContent(
    dayPlan: DayPlan,
    isToday: Boolean,
    onOpenAddSheet: (LocalDate, String) -> Unit,
    onOpenEditDialog: (PlannedMeal) -> Unit,
    onRequestRemove: (PlannedMeal) -> Unit,
    onCopyFromPreviousDay: (LocalDate, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            DaySummaryCard(dayPlan = dayPlan, isToday = isToday)
        }
        items(items = PLAN_CATEGORIES) { category ->
            CategorySection(
                category = category,
                date = dayPlan.date,
                plannedMeals = dayPlan.mealsForCategory(category),
                onAddClick = { onOpenAddSheet(dayPlan.date, category) },
                onCopyFromPreviousDay = { onCopyFromPreviousDay(dayPlan.date, category) },
                onMealClick = onOpenEditDialog,
                onRequestRemove = onRequestRemove,
            )
        }
    }
}

@Composable
private fun DaySummaryCard(dayPlan: DayPlan, isToday: Boolean) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dayPlan.date.format(FULL_DAY_FORMATTER)
                        .replaceFirstChar { it.titlecase(POLISH_LOCALE) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (isToday) {
                    Text(
                        text = "Dziś",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (dayPlan.plannedMeals.isEmpty()) {
                Text(
                    text = "Brak zaplanowanych posiłków.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                NutritionMacroBars(nutrition = dayPlan.nutrition)
            }
        }
    }
}

@Composable
private fun CategorySection(
    category: String,
    date: LocalDate,
    plannedMeals: List<PlannedMeal>,
    onAddClick: () -> Unit,
    onCopyFromPreviousDay: () -> Unit,
    onMealClick: (PlannedMeal) -> Unit,
    onRequestRemove: (PlannedMeal) -> Unit,
) {
    val categoryColor = mealCategoryColor(category)
    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = mealCategoryIcon(category),
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                IconButton(onClick = onCopyFromPreviousDay) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Kopiuj z wczoraj",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(" Dodaj")
                }
            }
            if (plannedMeals.isNotEmpty()) {
                HorizontalDivider()
                plannedMeals.forEachIndexed { index, plannedMeal ->
                    SwipeToDeleteContainer(onDeleteRequest = { onRequestRemove(plannedMeal) }) {
                        PlannedMealRow(
                            plannedMeal = plannedMeal,
                            onClick = { onMealClick(plannedMeal) },
                        )
                    }
                    if (index < plannedMeals.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun PlannedMealRow(plannedMeal: PlannedMeal, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = plannedMeal.meal.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${plannedMeal.servings.formatServings()} × porcji · ${plannedMeal.nutrition.calories.formatKcal()} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMealBottomSheet(
    state: AddMealSheetState,
    availableMeals: List<Meal>,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onMealTypeChange: (String) -> Unit,
    onShowAllCategoriesChange: (Boolean) -> Unit,
    onSelectMeal: (Meal) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (state.swapPlannedMealId != null) "Zamień posiłek" else "Zaplanuj posiłek",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = state.date.format(FULL_DAY_FORMATTER)
                    .replaceFirstChar { it.titlecase(POLISH_LOCALE) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.swapPlannedMealId == null) {
                MealTypeSelector(
                    selectedType = state.mealType,
                    onTypeChange = onMealTypeChange,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = mealCategoryIcon(state.mealType),
                        contentDescription = null,
                        tint = mealCategoryColor(state.mealType),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = state.mealType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = mealCategoryColor(state.mealType),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            AppSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
                placeholder = "Szukaj posiłku",
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = !state.showAllCategories,
                    onClick = { onShowAllCategoriesChange(false) },
                    label = { Text("Tylko: ${state.mealType}") },
                    leadingIcon = if (!state.showAllCategories) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                )
                FilterChip(
                    selected = state.showAllCategories,
                    onClick = { onShowAllCategoriesChange(true) },
                    label = { Text("Wszystkie") },
                    leadingIcon = if (state.showAllCategories) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                )
            }
            MealPicker(
                meals = availableMeals,
                onSelect = onSelectMeal,
                emptyHint = if (state.showAllCategories) {
                    "Brak posiłków. Dodaj posiłek w zakładce \"Posiłki\"."
                } else {
                    "Brak posiłków w kategorii \"${state.mealType}\". Wybierz \"Wszystkie\" lub dodaj posiłek."
                },
            )
        }
    }
}

private val SERVING_OPTIONS: List<Double> = listOf(1.0, 1.5, 2.0)

@Composable
private fun ServingsDialog(
    state: ServingsDialogState,
    onDismiss: () -> Unit,
    onServingsChange: (Double) -> Unit,
    onConfirm: () -> Unit,
) {
    ServingsPickerDialog(
        meal = state.meal,
        servings = state.servings,
        errorMessage = state.errorMessage,
        confirmLabel = "Dodaj",
        onServingsChange = onServingsChange,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
private fun EditServingsDialog(
    state: EditServingsDialogState,
    onDismiss: () -> Unit,
    onServingsChange: (Double) -> Unit,
    onConfirm: () -> Unit,
    onRemove: () -> Unit,
    onSwap: () -> Unit,
) {
    val repeatHint = state.repeatsOnDays.takeIf { it.isNotEmpty() }?.let { days ->
        val parts = days.map { day ->
            when (day) {
                state.date.minusDays(1) -> "dzień wcześniej"
                state.date.plusDays(1) -> "dzień później"
                else -> day.format(FULL_DAY_FORMATTER)
            }
        }
        "Ten posiłek jest w planie też ${parts.joinToString(" i ")} — " +
            "możesz przyrządzić większą porcję na kilka dni."
    }
    ServingsPickerDialog(
        meal = state.meal,
        servings = state.servings,
        errorMessage = state.errorMessage,
        confirmLabel = "Zapisz",
        hint = repeatHint,
        onServingsChange = onServingsChange,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        onRemove = onRemove,
        onSwap = onSwap,
    )
}

@Composable
private fun ServingsPickerDialog(
    meal: Meal,
    servings: Double,
    errorMessage: String?,
    confirmLabel: String,
    onServingsChange: (Double) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onRemove: (() -> Unit)? = null,
    onSwap: (() -> Unit)? = null,
    hint: String? = null,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(meal.name) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                hint?.let { hintText ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = hintText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Text(
                    text = "Liczba porcji",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SERVING_OPTIONS.forEach { option ->
                        FilterChip(
                            selected = servings == option,
                            onClick = { onServingsChange(option) },
                            label = { Text("${option.formatServings()}×") },
                        )
                    }
                }
                HorizontalDivider()
                Text(
                    text = "Wartości za ${servings.formatServings()} porcji",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                NutritionMacroBars(nutrition = meal.nutrition.times(servings))
                if (meal.ingredients.isNotEmpty()) {
                    HorizontalDivider()
                    Text(
                        text = "Składniki",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    meal.ingredients.forEach { ingredient ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = ingredient.product.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${(ingredient.quantityGrams * servings).formatGrams()} g",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                meal.description?.takeIf { it.isNotBlank() }?.let { description ->
                    HorizontalDivider()
                    Text(
                        text = "Przygotowanie",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (onSwap != null) {
                    TextButton(
                        onClick = onSwap,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Zamień posiłek")
                    }
                }
                if (onRemove != null) {
                    TextButton(
                        onClick = onRemove,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Usuń z planu", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealTypeSelector(
    selectedType: String,
    onTypeChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            PLAN_CATEGORIES.forEachIndexed { index, category ->
                SegmentedButton(
                    selected = selectedType == category,
                    onClick = { onTypeChange(category) },
                    shape = SegmentedButtonDefaults.itemShape(index, PLAN_CATEGORIES.size),
                    icon = { SegmentedButtonDefaults.Icon(active = selectedType == category) },
                    label = {
                        Icon(
                            imageVector = mealCategoryIcon(category),
                            contentDescription = category,
                        )
                    },
                )
            }
        }
        Text(
            text = selectedType,
            style = MaterialTheme.typography.bodySmall,
            color = mealCategoryColor(selectedType),
        )
    }
}

@Composable
private fun MealPicker(
    meals: List<Meal>,
    onSelect: (Meal) -> Unit,
    emptyHint: String = "Brak posiłków. Dodaj posiłek w zakładce \"Posiłki\".",
) {
    if (meals.isEmpty()) {
        Text(
            text = emptyHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        return
    }
    Card {
        meals.forEachIndexed { index, meal ->
            ListItem(
                headlineContent = { Text(meal.name) },
                supportingContent = {
                    Text(
                        text = "${meal.category} · ${meal.nutrition.calories.formatKcal()} kcal / porcja",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = mealCategoryIcon(meal.category),
                        contentDescription = null,
                        tint = mealCategoryColor(meal.category),
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.clickable { onSelect(meal) },
            )
            if (index < meals.lastIndex) HorizontalDivider()
        }
    }
}

private fun mealCategoryColor(category: String): Color = when (category) {
    "Śniadanie" -> MealColorSniadanie
    "Drugie śniadanie" -> MealColorDrugieSniadanie
    "Obiad" -> MealColorObiad
    "Kolacja" -> MealColorKolacja
    "Podwieczorek" -> MealColorPodwieczorek
    else -> MealColorObiad
}

private fun Double.formatKcal(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.0f".format(this)

private fun Double.formatServings(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)

private fun Double.formatGrams(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)
