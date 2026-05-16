package pl.pawel.diet_app_mobile.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
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
import pl.pawel.diet_app_mobile.domain.model.DayPlan
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.domain.repository.MealPlanRepository
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.ui.meals.MEAL_CATEGORIES

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
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

    private val _editSheet = MutableStateFlow<EditPlannedMealSheetState?>(null)
    val editSheet: StateFlow<EditPlannedMealSheetState?> = _editSheet.asStateFlow()

    val availableMeals: StateFlow<List<Meal>> = combine(allMeals, _addSheet) { meals, sheet ->
        if (sheet == null) return@combine emptyList()
        val query = sheet.query.trim()
        if (query.isBlank()) {
            meals
        } else {
            meals.filter { it.name.contains(query, ignoreCase = true) }
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

    fun onAddServingsChange(value: String) {
        _addSheet.update { it?.copy(servings = value, errorMessage = null) }
    }

    fun onSelectMeal(meal: Meal) {
        _addSheet.update { it?.copy(selectedMealId = meal.id, errorMessage = null) }
    }

    fun confirmAdd() {
        val sheet = _addSheet.value ?: return
        val mealId = sheet.selectedMealId
        val servings = sheet.servings.replace(',', '.').toDoubleOrNull()

        if (mealId == null || servings == null || servings <= 0.0) {
            _addSheet.update { it?.copy(errorMessage = "Wybierz posiłek i podaj liczbę porcji.") }
            return
        }

        viewModelScope.launch {
            runCatching {
                mealPlanRepository.addPlannedMeal(
                    weekStartDate = _weekStartDate.value,
                    mealId = mealId,
                    date = sheet.date,
                    mealType = sheet.mealType,
                    servings = servings,
                )
            }
                .onSuccess { _addSheet.value = null }
                .onFailure {
                    _addSheet.update {
                        it?.copy(errorMessage = "Nie udało się zaplanować posiłku.")
                    }
                }
        }
    }

    fun openEditSheet(plannedMeal: PlannedMeal) {
        _editSheet.value = EditPlannedMealSheetState(
            plannedMealId = plannedMeal.id,
            mealName = plannedMeal.meal.name,
            servings = plannedMeal.servings.toPlainString(),
        )
    }

    fun closeEditSheet() {
        _editSheet.value = null
    }

    fun onEditServingsChange(value: String) {
        _editSheet.update { it?.copy(servings = value, errorMessage = null) }
    }

    fun confirmEdit() {
        val sheet = _editSheet.value ?: return
        val servings = sheet.servings.replace(',', '.').toDoubleOrNull()

        if (servings == null || servings <= 0.0) {
            _editSheet.update { it?.copy(errorMessage = "Podaj poprawną liczbę porcji.") }
            return
        }

        viewModelScope.launch {
            runCatching {
                mealPlanRepository.updatePlannedMealServings(sheet.plannedMealId, servings)
            }
                .onSuccess { _editSheet.value = null }
                .onFailure {
                    _editSheet.update {
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

    fun removePlannedMealFromEdit() {
        val sheet = _editSheet.value ?: return
        _editSheet.value = null
        removePlannedMeal(sheet.plannedMealId)
    }
}

data class AddMealSheetState(
    val date: LocalDate,
    val mealType: String,
    val query: String = "",
    val selectedMealId: Long? = null,
    val servings: String = "1",
    val errorMessage: String? = null,
)

data class EditPlannedMealSheetState(
    val plannedMealId: Long,
    val mealName: String,
    val servings: String,
    val errorMessage: String? = null,
)

private fun emptyPlan(weekStart: LocalDate): MealPlan = MealPlan(
    id = 0,
    weekStartDate = weekStart,
    days = (0L..6L).map { offset ->
        DayPlan(date = weekStart.plusDays(offset), plannedMeals = emptyList())
    },
)

internal fun LocalDate.mondayOfWeek(): LocalDate = with(DayOfWeek.MONDAY)

private fun Double.toPlainString(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

internal val PLAN_CATEGORIES: List<String> = MEAL_CATEGORIES
