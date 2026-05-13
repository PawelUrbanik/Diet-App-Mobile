package pl.pawel.diet_app_mobile.ui.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.local.seed.MealSeeder
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealIngredient
import pl.pawel.diet_app_mobile.domain.model.NutritionSummary
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.domain.model.nutritionForGrams
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.domain.repository.ProductRepository

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    productRepository: ProductRepository,
    private val mealSeeder: MealSeeder,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val meals: StateFlow<List<Meal>> = mealRepository.observeMeals()
        .combine(searchQuery) { meals, query ->
            val normalizedQuery = query.trim()
            if (normalizedQuery.isBlank()) {
                meals
            } else {
                meals.filter { meal ->
                    meal.name.contains(normalizedQuery, ignoreCase = true) ||
                        meal.category.contains(normalizedQuery, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val query: StateFlow<String> = searchQuery.asStateFlow()

    private val products: StateFlow<List<Product>> = productRepository.observeProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _formState = MutableStateFlow(MealFormState())
    val formState: StateFlow<MealFormState> = _formState.asStateFlow()

    private val _editorState = MutableStateFlow<MealEditorState?>(null)
    val editorState: StateFlow<MealEditorState?> = _editorState.asStateFlow()

    private val _ingredientFormState = MutableStateFlow(IngredientFormState())
    val ingredientFormState: StateFlow<IngredientFormState> = _ingredientFormState.asStateFlow()

    val ingredientProducts: StateFlow<List<Product>> = products
        .combine(_ingredientFormState) { products, formState ->
            val query = formState.productQuery.trim()
            val filteredProducts = if (query.isBlank()) {
                products
            } else {
                products.filter { product ->
                    product.name.contains(query, ignoreCase = true)
                }
            }
            filteredProducts.take(PRODUCT_RESULT_LIMIT)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            mealSeeder.seedMissingMeals()
        }
    }

    fun onNameChange(value: String) = updateForm { copy(name = value, errorMessage = null) }

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

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

    fun openMealEditor(meal: Meal) {
        _editorState.value = meal.toEditorState()
        _ingredientFormState.value = IngredientFormState()
    }

    fun closeMealEditor() {
        _editorState.value = null
        _ingredientFormState.value = IngredientFormState()
    }

    fun onEditorNameChange(value: String) = updateEditor { copy(name = value, errorMessage = null) }

    fun onEditorCategoryChange(value: String) = updateEditor { copy(category = value, errorMessage = null) }

    fun onEditorDescriptionChange(value: String) = updateEditor {
        copy(description = value, errorMessage = null)
    }

    fun onProductQueryChange(value: String) {
        _ingredientFormState.update {
            it.copy(productQuery = value, selectedProductId = null, errorMessage = null)
        }
    }

    fun selectIngredientProduct(product: Product) {
        _ingredientFormState.update {
            it.copy(
                productQuery = product.name,
                selectedProductId = product.id,
                errorMessage = null,
            )
        }
    }

    fun onQuantityGramsChange(value: String) {
        _ingredientFormState.update { it.copy(quantityGrams = value, errorMessage = null) }
    }

    fun addIngredientToDraft() {
        val editor = editorState.value
        val state = ingredientFormState.value
        val selectedProduct = products.value.firstOrNull { product -> product.id == state.selectedProductId }
        val quantityGrams = state.quantityGrams.toDoubleOrNull()

        if (editor == null || selectedProduct == null || quantityGrams == null || quantityGrams <= 0.0) {
            _ingredientFormState.update {
                it.copy(errorMessage = "Wybierz produkt i podaj poprawną gramaturę.")
            }
            return
        }

        val ingredient = MealIngredient(
            mealId = editor.id,
            product = selectedProduct,
            quantityGrams = quantityGrams,
            nutrition = selectedProduct.nutritionForGrams(quantityGrams),
        )

        updateEditor {
            copy(
                ingredients = ingredients + ingredient,
                errorMessage = null,
            )
        }
        _ingredientFormState.value = IngredientFormState()
    }

    fun removeIngredientFromDraft(index: Int) {
        updateEditor {
            copy(
                ingredients = ingredients.filterIndexed { ingredientIndex, _ ->
                    ingredientIndex != index
                },
                errorMessage = null,
            )
        }
    }

    fun saveMealChanges() {
        val editor = editorState.value ?: return
        val meal = editor.toMealOrNull()

        if (meal == null) {
            updateEditor { copy(errorMessage = "Podaj nazwę i wybierz typ posiłku.") }
            return
        }

        viewModelScope.launch {
            updateEditor { copy(isSaving = true, errorMessage = null) }
            runCatching { mealRepository.updateMeal(meal) }
                .onSuccess { closeMealEditor() }
                .onFailure {
                    updateEditor {
                        copy(
                            isSaving = false,
                            errorMessage = "Nie udało się zapisać zmian.",
                        )
                    }
                }
        }
    }

    private fun updateForm(update: MealFormState.() -> MealFormState) {
        _formState.update(update)
    }

    private fun updateEditor(update: MealEditorState.() -> MealEditorState) {
        _editorState.update { state -> state?.update() }
    }

    private companion object {
        const val PRODUCT_RESULT_LIMIT = 8
    }
}

data class MealFormState(
    val name: String = "",
    val category: String = MEAL_CATEGORIES.first(),
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

data class MealEditorState(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val ingredients: List<MealIngredient>,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val nutrition: NutritionSummary
        get() = ingredients.fold(NutritionSummary()) { total, ingredient ->
            total + ingredient.nutrition
        }
}

data class IngredientFormState(
    val productQuery: String = "",
    val selectedProductId: Long? = null,
    val quantityGrams: String = "",
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

private fun Meal.toEditorState(): MealEditorState = MealEditorState(
    id = id,
    name = name,
    category = category,
    description = description.orEmpty(),
    ingredients = ingredients,
)

private fun MealEditorState.toMealOrNull(): Meal? {
    if (name.isBlank() || category.isBlank()) return null

    return Meal(
        id = id,
        name = name.trim(),
        category = category,
        description = description.trim().ifBlank { null },
        ingredients = ingredients,
        nutrition = nutrition,
    )
}
