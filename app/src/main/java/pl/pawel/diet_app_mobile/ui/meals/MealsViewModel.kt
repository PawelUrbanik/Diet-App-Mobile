package pl.pawel.diet_app_mobile.ui.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.repository.MealRepository

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val mealRepository: MealRepository,
) : ViewModel() {
    val meals: StateFlow<List<Meal>> = mealRepository.observeMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _formState = MutableStateFlow(MealFormState())
    val formState: StateFlow<MealFormState> = _formState.asStateFlow()

    fun onNameChange(value: String) = updateForm { copy(name = value, errorMessage = null) }

    fun onCategoryChange(value: String) = updateForm { copy(category = value, errorMessage = null) }

    fun onDescriptionChange(value: String) = updateForm { copy(description = value, errorMessage = null) }

    fun addMeal() {
        val state = formState.value
        val meal = state.toMealOrNull()

        if (meal == null) {
            updateForm { copy(errorMessage = "Podaj nazwę i wybierz typ posiłku.") }
            return
        }

        viewModelScope.launch {
            updateForm { copy(isSaving = true, errorMessage = null) }
            runCatching { mealRepository.addMeal(meal) }
                .onSuccess { _formState.value = MealFormState() }
                .onFailure {
                    updateForm {
                        copy(
                            isSaving = false,
                            errorMessage = "Nie udało się dodać posiłku.",
                        )
                    }
                }
        }
    }

    private fun updateForm(update: MealFormState.() -> MealFormState) {
        _formState.update(update)
    }
}

data class MealFormState(
    val name: String = "",
    val category: String = MEAL_CATEGORIES.first(),
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

val MEAL_CATEGORIES = listOf(
    "Śniadanie",
    "Drugie śniadanie",
    "Obiad",
    "Kolacja",
    "Przekąska",
)

private fun MealFormState.toMealOrNull(): Meal? {
    if (name.isBlank() || category.isBlank()) return null

    return Meal(
        name = name.trim(),
        category = category,
        description = description.trim().ifBlank { null },
    )
}
