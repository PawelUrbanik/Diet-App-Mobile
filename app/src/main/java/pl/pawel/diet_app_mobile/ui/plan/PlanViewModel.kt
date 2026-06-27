package pl.pawel.diet_app_mobile.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
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
import pl.pawel.diet_app_mobile.data.share.WeekPlanCodec
import pl.pawel.diet_app_mobile.domain.model.DayPlan
import pl.pawel.diet_app_mobile.domain.model.ImportWeekResult
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.domain.model.WeekShare
import pl.pawel.diet_app_mobile.domain.model.WeekShareSlot
import pl.pawel.diet_app_mobile.domain.model.WeekTemplate
import pl.pawel.diet_app_mobile.domain.repository.MealPlanRepository
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.domain.repository.WeekTemplateRepository
import pl.pawel.diet_app_mobile.ui.meals.MEAL_CATEGORIES

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val weekTemplateRepository: WeekTemplateRepository,
    mealRepository: MealRepository,
) : ViewModel() {
    private val _weekStartDate = MutableStateFlow(LocalDate.now().mondayOfWeek())
    val weekStartDate: StateFlow<LocalDate> = _weekStartDate.asStateFlow()

    val plan: StateFlow<MealPlan> = _weekStartDate
        .flatMapLatest { weekStart ->
            mealPlanRepository.observeWeekPlan(weekStart)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyPlan(_weekStartDate.value),
        )

    private val allMeals: StateFlow<List<Meal>> = mealRepository.observeMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _addSheet = MutableStateFlow<AddMealSheetState?>(null)
    val addSheet: StateFlow<AddMealSheetState?> = _addSheet.asStateFlow()

    private val _servingsDialog = MutableStateFlow<ServingsDialogState?>(null)
    val servingsDialog: StateFlow<ServingsDialogState?> = _servingsDialog.asStateFlow()

    private val _editDialog = MutableStateFlow<EditServingsDialogState?>(null)
    val editDialog: StateFlow<EditServingsDialogState?> = _editDialog.asStateFlow()

    val templates: StateFlow<List<WeekTemplate>> = weekTemplateRepository.observeTemplates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _templatesSheet = MutableStateFlow<TemplatesSheetMode?>(null)
    val templatesSheet: StateFlow<TemplatesSheetMode?> = _templatesSheet.asStateFlow()

    private val _saveTemplateDialog = MutableStateFlow<SaveTemplateDialogState?>(null)
    val saveTemplateDialog: StateFlow<SaveTemplateDialogState?> = _saveTemplateDialog.asStateFlow()

    private val _applyConfirm = MutableStateFlow<ApplyTemplateConfirmState?>(null)
    val applyConfirm: StateFlow<ApplyTemplateConfirmState?> = _applyConfirm.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _shareQr = MutableStateFlow<ShareQrState?>(null)
    val shareQr: StateFlow<ShareQrState?> = _shareQr.asStateFlow()

    private val _importPlan = MutableStateFlow<ImportPlanState?>(null)
    val importPlan: StateFlow<ImportPlanState?> = _importPlan.asStateFlow()

    val availableMeals: StateFlow<List<Meal>> = combine(allMeals, _addSheet) { meals, sheet ->
        if (sheet == null) return@combine emptyList()
        val query = sheet.query.trim()
        val byCategory = if (sheet.showAllCategories) {
            meals
        } else {
            meals.filter { it.category.equals(sheet.mealType, ignoreCase = true) }
        }
        if (query.isBlank()) {
            byCategory
        } else {
            byCategory.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun goToPreviousWeek() {
        _weekStartDate.value = _weekStartDate.value.minusWeeks(1)
    }

    fun goToNextWeek() {
        _weekStartDate.value = _weekStartDate.value.plusWeeks(1)
    }

    fun goToCurrentWeek() {
        _weekStartDate.value = LocalDate.now().mondayOfWeek()
    }

    fun openAddSheet(date: LocalDate, mealType: String) {
        _addSheet.value = AddMealSheetState(date = date, mealType = mealType)
    }

    fun closeAddSheet() {
        _addSheet.value = null
    }

    fun onAddQueryChange(value: String) {
        _addSheet.update { it?.copy(query = value) }
    }

    fun onAddMealTypeChange(value: String) {
        _addSheet.update { it?.copy(mealType = value) }
    }

    fun onShowAllCategoriesChange(value: Boolean) {
        _addSheet.update { it?.copy(showAllCategories = value) }
    }

    fun onSelectMeal(meal: Meal) {
        val sheet = _addSheet.value ?: return
        _servingsDialog.value = ServingsDialogState(
            date = sheet.date,
            mealType = sheet.mealType,
            meal = meal,
            servings = sheet.swapDefaultServings,
            swapPlannedMealId = sheet.swapPlannedMealId,
        )
    }

    fun closeServingsDialog() {
        _servingsDialog.value = null
    }

    fun onDialogServingsChange(value: Double) {
        _servingsDialog.update { it?.copy(servings = value, errorMessage = null) }
    }

    fun confirmAdd() {
        val dialog = _servingsDialog.value ?: return

        if (dialog.servings <= 0.0) {
            _servingsDialog.update { it?.copy(errorMessage = "Wybierz liczbę porcji.") }
            return
        }

        viewModelScope.launch {
            runCatching {
                if (dialog.swapPlannedMealId != null) {
                    mealPlanRepository.replacePlannedMeal(
                        plannedMealId = dialog.swapPlannedMealId,
                        newMealId = dialog.meal.id,
                        servings = dialog.servings,
                    )
                } else {
                    mealPlanRepository.addPlannedMeal(
                        weekStartDate = _weekStartDate.value,
                        mealId = dialog.meal.id,
                        date = dialog.date,
                        mealType = dialog.mealType,
                        servings = dialog.servings,
                    )
                }
            }
                .onSuccess {
                    _servingsDialog.value = null
                    _addSheet.value = null
                }
                .onFailure {
                    _servingsDialog.update {
                        it?.copy(errorMessage = "Nie udało się zaplanować posiłku.")
                    }
                }
        }
    }

    fun openSwapFromEdit() {
        val dialog = _editDialog.value ?: return
        _editDialog.value = null
        _addSheet.value = AddMealSheetState(
            date = dialog.date,
            mealType = dialog.mealType,
            swapPlannedMealId = dialog.plannedMealId,
            swapDefaultServings = dialog.servings,
        )
    }

    fun openEditDialog(plannedMeal: PlannedMeal) {
        _editDialog.value = EditServingsDialogState(
            plannedMealId = plannedMeal.id,
            meal = plannedMeal.meal,
            servings = plannedMeal.servings,
            date = plannedMeal.date,
            mealType = plannedMeal.mealType,
        )
        viewModelScope.launch {
            val days = runCatching {
                mealPlanRepository.adjacentDaysWithSameMeal(plannedMeal.meal.id, plannedMeal.date)
            }.getOrDefault(emptyList())
            if (days.isNotEmpty()) {
                _editDialog.update { state ->
                    if (state?.plannedMealId == plannedMeal.id) {
                        state.copy(repeatsOnDays = days)
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun closeEditDialog() {
        _editDialog.value = null
    }

    fun onEditServingsChange(value: Double) {
        _editDialog.update { it?.copy(servings = value, errorMessage = null) }
    }

    fun confirmEdit() {
        val dialog = _editDialog.value ?: return

        if (dialog.servings <= 0.0) {
            _editDialog.update { it?.copy(errorMessage = "Wybierz liczbę porcji.") }
            return
        }

        viewModelScope.launch {
            runCatching {
                mealPlanRepository.updatePlannedMealServings(dialog.plannedMealId, dialog.servings)
            }
                .onSuccess { _editDialog.value = null }
                .onFailure {
                    _editDialog.update {
                        it?.copy(errorMessage = "Nie udało się zapisać zmian.")
                    }
                }
        }
    }

    fun removePlannedMeal(plannedMealId: Long) {
        viewModelScope.launch {
            runCatching { mealPlanRepository.removePlannedMeal(plannedMealId) }
        }
    }

    fun onToggleSlotSkipped(date: LocalDate, mealType: String, skipped: Boolean) {
        viewModelScope.launch {
            runCatching { mealPlanRepository.setSlotSkipped(date, mealType, skipped) }
        }
    }

    fun copyFromPreviousDay(date: LocalDate, mealType: String) {
        val sourceDate = date.minusDays(1)
        val targetWeekStart = date.mondayOfWeek()
        viewModelScope.launch {
            runCatching {
                mealPlanRepository.copyDayCategory(
                    sourceDate = sourceDate,
                    targetDate = date,
                    targetWeekStartDate = targetWeekStart,
                    mealType = mealType,
                )
            }
        }
    }

    fun removePlannedMealFromEdit() {
        val dialog = _editDialog.value ?: return
        _editDialog.value = null
        removePlannedMeal(dialog.plannedMealId)
    }

    fun openApplyTemplatesSheet() {
        _templatesSheet.value = TemplatesSheetMode.Apply
    }

    fun closeTemplatesSheet() {
        _templatesSheet.value = null
    }

    fun requestApplyTemplate(template: WeekTemplate) {
        val existing = plan.value.days.sumOf { it.plannedMeals.size }
        _applyConfirm.value = ApplyTemplateConfirmState(
            template = template,
            existingMealsCount = existing,
        )
    }

    fun cancelApplyTemplate() {
        _applyConfirm.value = null
    }

    fun confirmApplyTemplate() {
        val confirm = _applyConfirm.value ?: return
        val target = _weekStartDate.value
        _applyConfirm.value = null
        _templatesSheet.value = null
        viewModelScope.launch {
            runCatching {
                weekTemplateRepository.applyTemplate(confirm.template.id, target)
            }
                .onSuccess {
                    _message.value = "Zastosowano szablon „${confirm.template.name}”."
                }
                .onFailure {
                    _message.value = "Nie udało się zastosować szablonu."
                }
        }
    }

    fun openSaveTemplateDialog() {
        _saveTemplateDialog.value = SaveTemplateDialogState()
    }

    fun closeSaveTemplateDialog() {
        _saveTemplateDialog.value = null
    }

    fun onSaveTemplateNameChange(value: String) {
        _saveTemplateDialog.value = _saveTemplateDialog.value?.copy(name = value, errorMessage = null)
    }

    fun confirmSaveTemplate() {
        val dialog = _saveTemplateDialog.value ?: return
        val name = dialog.name.trim()
        if (name.isBlank()) {
            _saveTemplateDialog.value = dialog.copy(errorMessage = "Podaj nazwę szablonu.")
            return
        }
        val weekStart = _weekStartDate.value
        viewModelScope.launch {
            runCatching {
                weekTemplateRepository.saveCurrentWeekAsTemplate(name, weekStart)
            }
                .onSuccess { count ->
                    if (count == 0) {
                        _saveTemplateDialog.value = dialog.copy(
                            errorMessage = "Tydzień jest pusty — nie ma czego zapisać.",
                        )
                    } else {
                        _saveTemplateDialog.value = null
                        _message.value = "Zapisano szablon „$name” ($count posiłków)."
                    }
                }
                .onFailure {
                    _saveTemplateDialog.value = dialog.copy(
                        errorMessage = "Nie udało się zapisać szablonu.",
                    )
                }
        }
    }

    fun deleteTemplate(template: WeekTemplate) {
        if (template.isPredefined) return
        viewModelScope.launch {
            runCatching { weekTemplateRepository.deleteTemplate(template.id) }
                .onSuccess { _message.value = "Usunięto szablon „${template.name}”." }
                .onFailure { _message.value = "Nie udało się usunąć szablonu." }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    fun openShareQr() {
        val share = buildCurrentWeekShare()
        if (share.slots.isEmpty()) {
            _message.value = "Tydzień jest pusty — nie ma czego udostępnić."
            return
        }
        _shareQr.value = ShareQrState(
            payload = WeekPlanCodec.encode(share),
            label = share.label,
            mealCount = share.slots.size,
        )
    }

    fun closeShareQr() {
        _shareQr.value = null
    }

    fun onPlanScanned(text: String) {
        val share = WeekPlanCodec.decode(text)
        if (share == null || share.slots.isEmpty()) {
            _message.value = "Nieprawidłowy kod planu."
            return
        }
        _importPlan.value = ImportPlanState(share = share, targetWeekStart = _weekStartDate.value)
    }

    fun onImportWeekChange(weekStart: LocalDate) {
        _importPlan.update { it?.copy(targetWeekStart = weekStart.mondayOfWeek()) }
    }

    fun cancelImport() {
        _importPlan.value = null
    }

    fun confirmImport() {
        val state = _importPlan.value ?: return
        val target = state.targetWeekStart
        _importPlan.value = null
        viewModelScope.launch {
            runCatching { mealPlanRepository.applySharedWeek(target, state.share.slots) }
                .onSuccess { result ->
                    _weekStartDate.value = target
                    _message.value = buildImportMessage(result)
                }
                .onFailure { _message.value = "Nie udało się zastosować planu." }
        }
    }

    private fun buildCurrentWeekShare(): WeekShare {
        val currentPlan = plan.value
        val weekStart = _weekStartDate.value
        val slots = currentPlan.days.flatMap { day ->
            val offset = (day.date.toEpochDay() - weekStart.toEpochDay()).toInt()
            day.plannedMeals.map { planned ->
                WeekShareSlot(
                    dayOffset = offset,
                    mealType = planned.mealType,
                    mealName = planned.meal.name,
                    servings = planned.servings,
                )
            }
        }
        val label = "${weekStart.format(SHARE_DATE_FORMATTER)}–" +
            weekStart.plusDays(6).format(SHARE_DATE_FORMATTER)
        return WeekShare(label = label, slots = slots)
    }

    private fun buildImportMessage(result: ImportWeekResult): String {
        val applied = "Zastosowano plan (${result.applied} posiłków)."
        if (result.skipped.isEmpty()) return applied
        val shown = result.skipped.take(3).joinToString(", ")
        val more = if (result.skipped.size > 3) " i ${result.skipped.size - 3} więcej" else ""
        return "$applied Pominięto: $shown$more."
    }
}

data class AddMealSheetState(
    val date: LocalDate,
    val mealType: String,
    val query: String = "",
    val showAllCategories: Boolean = false,
    val swapPlannedMealId: Long? = null,
    val swapDefaultServings: Double = 1.0,
)

data class ServingsDialogState(
    val date: LocalDate,
    val mealType: String,
    val meal: Meal,
    val servings: Double = 1.0,
    val errorMessage: String? = null,
    val swapPlannedMealId: Long? = null,
)

data class EditServingsDialogState(
    val plannedMealId: Long,
    val meal: Meal,
    val servings: Double,
    val date: LocalDate,
    val mealType: String,
    val errorMessage: String? = null,
    val repeatsOnDays: List<LocalDate> = emptyList(),
)

enum class TemplatesSheetMode { Apply }

data class SaveTemplateDialogState(
    val name: String = "",
    val errorMessage: String? = null,
)

data class ApplyTemplateConfirmState(
    val template: WeekTemplate,
    val existingMealsCount: Int,
)

data class ShareQrState(
    val payload: String,
    val label: String,
    val mealCount: Int,
)

data class ImportPlanState(
    val share: WeekShare,
    val targetWeekStart: LocalDate,
)

private val SHARE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

private fun emptyPlan(weekStart: LocalDate): MealPlan = MealPlan(
    id = 0,
    weekStartDate = weekStart,
    days = (0L..6L).map { offset ->
        DayPlan(date = weekStart.plusDays(offset), plannedMeals = emptyList())
    },
)

internal fun LocalDate.mondayOfWeek(): LocalDate = with(DayOfWeek.MONDAY)

internal val PLAN_CATEGORIES: List<String> = MEAL_CATEGORIES
