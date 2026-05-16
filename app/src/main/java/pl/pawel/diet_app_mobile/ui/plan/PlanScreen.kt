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
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import pl.pawel.diet_app_mobile.domain.model.DayPlan
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.ui.components.AppSearchBar
import pl.pawel.diet_app_mobile.ui.components.NutritionMacroBars
import pl.pawel.diet_app_mobile.ui.components.SwipeToDeleteContainer
import pl.pawel.diet_app_mobile.ui.components.mealCategoryIcon
import pl.pawel.diet_app_mobile.ui.theme.MealColorDrugieSniadanie
import pl.pawel.diet_app_mobile.ui.theme.MealColorKolacja
import pl.pawel.diet_app_mobile.ui.theme.MealColorObiad
import pl.pawel.diet_app_mobile.ui.theme.MealColorPrzekaski
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

    PlanScreen(
        plan = plan,
        weekStartDate = weekStartDate,
        addSheet = addSheet,
        servingsDialog = servingsDialog,
        editDialog = editDialog,
        availableMeals = availableMeals,
        onPreviousWeek = viewModel::goToPreviousWeek,
        onNextWeek = viewModel::goToNextWeek,
        onToday = viewModel::goToCurrentWeek,
        onOpenAddSheet = viewModel::openAddSheet,
        onCloseAddSheet = viewModel::closeAddSheet,
        onAddQueryChange = viewModel::onAddQueryChange,
        onAddMealTypeChange = viewModel::onAddMealTypeChange,
        onSelectMeal = viewModel::onSelectMeal,
        onCloseServingsDialog = viewModel::closeServingsDialog,
        onDialogServingsChange = viewModel::onDialogServingsChange,
        onConfirmAdd = viewModel::confirmAdd,
        onOpenEditDialog = viewModel::openEditDialog,
        onCloseEditDialog = viewModel::closeEditDialog,
        onEditServingsChange = viewModel::onEditServingsChange,
        onConfirmEdit = viewModel::confirmEdit,
        onRemoveFromEdit = viewModel::removePlannedMealFromEdit,
        onSwipeRemove = viewModel::removePlannedMeal,
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
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToday: () -> Unit,
    onOpenAddSheet: (LocalDate, String) -> Unit,
    onCloseAddSheet: () -> Unit,
    onAddQueryChange: (String) -> Unit,
    onAddMealTypeChange: (String) -> Unit,
    onSelectMeal: (Meal) -> Unit,
    onCloseServingsDialog: () -> Unit,
    onDialogServingsChange: (Double) -> Unit,
    onConfirmAdd: () -> Unit,
    onOpenEditDialog: (PlannedMeal) -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditServingsChange: (Double) -> Unit,
    onConfirmEdit: () -> Unit,
    onRemoveFromEdit: () -> Unit,
    onSwipeRemove: (Long) -> Unit,
) {
    val today = remember { LocalDate.now() }
    val initialPage = if (today in weekStartDate..weekStartDate.plusDays(6)) {
        today.dayOfWeek.value - 1
    } else {
        0
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 7 })

    LaunchedEffect(weekStartDate) {
        val newPage = if (today in weekStartDate..weekStartDate.plusDays(6)) {
            today.dayOfWeek.value - 1
        } else {
            0
        }
        pagerState.scrollToPage(newPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan") },
                actions = {
                    IconButton(onClick = onToday) {
                        Icon(Icons.Default.Today, contentDescription = "Bieżący tydzień")
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
                    onSwipeRemove = onSwipeRemove,
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
        )
    }
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
    onSwipeRemove: (Long) -> Unit,
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
                onMealClick = onOpenEditDialog,
                onSwipeRemove = onSwipeRemove,
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
    onMealClick: (PlannedMeal) -> Unit,
    onSwipeRemove: (Long) -> Unit,
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
                    SwipeToDeleteContainer(onDeleteRequest = { onSwipeRemove(plannedMeal.id) }) {
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
                text = "Zaplanuj posiłek",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = state.date.format(FULL_DAY_FORMATTER)
                    .replaceFirstChar { it.titlecase(POLISH_LOCALE) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MealTypeSelector(
                selectedType = state.mealType,
                onTypeChange = onMealTypeChange,
            )
            AppSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
                placeholder = "Szukaj posiłku",
                modifier = Modifier.fillMaxWidth(),
            )
            MealPicker(
                meals = availableMeals,
                onSelect = onSelectMeal,
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
) {
    ServingsPickerDialog(
        meal = state.meal,
        servings = state.servings,
        errorMessage = state.errorMessage,
        confirmLabel = "Zapisz",
        onServingsChange = onServingsChange,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        onRemove = onRemove,
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
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(meal.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
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
) {
    if (meals.isEmpty()) {
        Text(
            text = "Brak posiłków. Dodaj posiłek w zakładce \"Posiłki\".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        return
    }
    val visibleMeals = meals.take(8)
    Card {
        visibleMeals.forEachIndexed { index, meal ->
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
            if (index < visibleMeals.lastIndex) HorizontalDivider()
        }
    }
}

private fun mealCategoryColor(category: String): Color = when (category) {
    "Śniadanie" -> MealColorSniadanie
    "Drugie śniadanie" -> MealColorDrugieSniadanie
    "Obiad" -> MealColorObiad
    "Kolacja" -> MealColorKolacja
    "Przekąska" -> MealColorPrzekaski
    else -> MealColorObiad
}

private fun Double.formatKcal(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.0f".format(this)

private fun Double.formatServings(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)
