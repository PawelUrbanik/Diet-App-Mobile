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

    private val _addMealState = MutableStateFlow<MealFormState?>(null)
    val addMealState: StateFlow<MealFormState?> = _addMealState.asStateFlow()

    private val _editorState = MutableStateFlow<MealEditorState?>(null)
    val editorState: StateFlow<MealEditorState?> = _editorState.asStateFlow()

    private val _ingredientEditorState = MutableStateFlow<IngredientEditorState?>(null)
    val ingredientEditorState: StateFlow<IngredientEditorState?> = _ingredientEditorState.asStateFlow()

    val ingredientProducts: StateFlow<List<Product>> = products
        .combine(_ingredientEditorState) { products, editorState ->
            if (editorState == null) return@combine emptyList()
            val query = editorState.productQuery.trim()
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

    fun openAddMeal() {
        _addMealState.value = MealFormState()
    }

    fun closeAddMeal() {
        _addMealState.value = null
    }

    fun onNameChange(value: String) = updateForm { copy(name = value, errorMessage = null) }

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onCategoryChange(value: String) = updateForm { copy(category = value, errorMessage = null) }

    fun onDescriptionChange(value: String) = updateForm { copy(description = value, errorMessage = null) }

    fun addMeal() {
        val state = addMealState.value ?: return
        val meal = state.toMealOrNull()

        if (meal == null) {
            updateForm { copy(errorMessage = "Podaj nazwę i wybierz typ posiłku.") }
            return
        }

        viewModelScope.launch {
            updateForm { copy(isSaving = true, errorMessage = null) }
            runCatching { mealRepository.addMeal(meal) }
                .onSuccess { _addMealState.value = null }
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
        _ingredientEditorState.value = null
    }

    fun closeMealEditor() {
        _editorState.value = null
        _ingredientEditorState.value = null
    }

    fun onEditorNameChange(value: String) = updateEditor { copy(name = value, errorMessage = null) }

    fun onEditorCategoryChange(value: String) = updateEditor { copy(category = value, errorMessage = null) }

    fun onEditorDescriptionChange(value: String) = updateEditor {
        copy(description = value, errorMessage = null)
    }

    fun openAddIngredient() {
        if (editorState.value == null) return
        _ingredientEditorState.value = IngredientEditorState(editIndex = null)
    }

    fun openEditIngredient(index: Int) {
        val editor = editorState.value ?: return
        val ingredient = editor.ingredients.getOrNull(index) ?: return
        _ingredientEditorState.value = IngredientEditorState(
            editIndex = index,
            productQuery = ingredient.product.name,
            selectedProductId = ingredient.product.id,
            quantityGrams = ingredient.quantityGrams.toPlainString(),
        )
    }

    fun closeIngredientEditor() {
        _ingredientEditorState.value = null
    }

    fun onProductQueryChange(value: String) {
        _ingredientEditorState.update { state ->
            state?.copy(productQuery = value, selectedProductId = null, errorMessage = null)
        }
    }

    fun selectIngredientProduct(product: Product) {
        _ingredientEditorState.update { state ->
            state?.copy(
                productQuery = product.name,
                selectedProductId = product.id,
                errorMessage = null,
            )
        }
    }

    fun onQuantityGramsChange(value: String) {
        _ingredientEditorState.update { state ->
            state?.copy(quantityGrams = value, errorMessage = null)
        }
    }

    fun saveIngredient() {
        val editor = editorState.value ?: return
        val state = ingredientEditorState.value ?: return
        val selectedProduct = products.value.firstOrNull { product -> product.id == state.selectedProductId }
        val quantityGrams = state.quantityGrams.replace(',', '.').toDoubleOrNull()

        if (selectedProduct == null || quantityGrams == null || quantityGrams <= 0.0) {
            _ingredientEditorState.update {
                it?.copy(errorMessage = "Wybierz produkt i podaj poprawną gramaturę.")
            }
            return
        }

        val ingredient = MealIngredient(
            mealId = editor.id,
            product = selectedProduct,
            quantityGrams = quantityGrams,
            nutrition = selectedProduct.nutritionForGrams(quantityGrams),
        )

        val editIndex = state.editIndex
        updateEditor {
            val newIngredients = if (editIndex != null && editIndex in ingredients.indices) {
                ingredients.toMutableList().also { it[editIndex] = ingredient }
            } else {
                ingredients + ingredient
            }
            copy(ingredients = newIngredients, errorMessage = null)
        }
        _ingredientEditorState.value = null
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

    fun removeIngredientFromEditor() {
        val state = ingredientEditorState.value ?: return
        val index = state.editIndex ?: return
        removeIngredientFromDraft(index)
        _ingredientEditorState.value = null
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
        _addMealState.update { state -> state?.update() }
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

data class IngredientEditorState(
    val editIndex: Int?,
    val productQuery: String = "",
    val selectedProductId: Long? = null,
    val quantityGrams: String = "",
    val errorMessage: String? = null,
) {
    val isEditing: Boolean get() = editIndex != null
}

private fun Double.toPlainString(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

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
