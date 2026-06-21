package pl.pawel.diet_app_mobile.ui.products

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
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.domain.repository.ProductRepository

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val _sortBy = MutableStateFlow(ProductSortBy.Name)

    val products: StateFlow<List<Product>> = combine(
        productRepository.observeProducts(),
        searchQuery,
        _sortBy,
    ) { products, query, sort ->
        val normalizedQuery = query.trim()
        val filtered = if (normalizedQuery.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(normalizedQuery, ignoreCase = true) }
        }
        when (sort) {
            ProductSortBy.Name -> filtered.sortedBy { it.name }
            ProductSortBy.Calories -> filtered.sortedByDescending { it.caloriesPer100g }
            ProductSortBy.Protein -> filtered.sortedByDescending { it.proteinPer100g }
            ProductSortBy.Fat -> filtered.sortedByDescending { it.fatPer100g }
            ProductSortBy.Carbs -> filtered.sortedByDescending { it.carbsPer100g }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val query: StateFlow<String> = searchQuery.asStateFlow()
    val sortBy: StateFlow<ProductSortBy> = _sortBy.asStateFlow()

    private val _editorState = MutableStateFlow<ProductFormState?>(null)
    val editorState: StateFlow<ProductFormState?> = _editorState.asStateFlow()

    private val _productPendingDelete = MutableStateFlow<Product?>(null)
    val productPendingDelete: StateFlow<Product?> = _productPendingDelete.asStateFlow()

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onSortByChange(sort: ProductSortBy) {
        _sortBy.value = sort
    }

    fun onNameChange(value: String) = updateForm { copy(name = value, errorMessage = null) }

    fun onCaloriesChange(value: String) = updateForm { copy(calories = value, errorMessage = null) }

    fun onProteinChange(value: String) = updateForm { copy(protein = value, errorMessage = null) }

    fun onFatChange(value: String) = updateForm { copy(fat = value, errorMessage = null) }

    fun onCarbsChange(value: String) = updateForm { copy(carbs = value, errorMessage = null) }

    fun openAddProduct() {
        _editorState.value = ProductFormState()
    }

    fun openEditProduct(product: Product) {
        _editorState.value = ProductFormState(
            editingProductId = product.id,
            name = product.name,
            calories = product.caloriesPer100g.formatInput(),
            protein = product.proteinPer100g.formatInput(),
            fat = product.fatPer100g.formatInput(),
            carbs = product.carbsPer100g.formatInput(),
        )
    }

    fun closeEditor() {
        _editorState.value = null
    }

    fun requestDeleteProduct(product: Product) {
        _productPendingDelete.value = product
    }

    fun cancelDeleteProduct() {
        _productPendingDelete.value = null
    }

    fun confirmDeleteProduct() {
        val product = _productPendingDelete.value ?: return
        _productPendingDelete.value = null
        viewModelScope.launch {
            runCatching { productRepository.deleteProduct(product.id) }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            runCatching { productRepository.deleteProduct(product.id) }
                .onSuccess {
                    if (editorState.value?.editingProductId == product.id) {
                        _editorState.value = null
                    }
                }
                .onFailure {
                    updateForm {
                        copy(errorMessage = "Nie udało się usunąć produktu. Może być używany w posiłku.")
                    }
                }
        }
    }

    fun deleteProductFromEditor() {
        val state = editorState.value ?: return
        val productId = state.editingProductId ?: return
        viewModelScope.launch {
            runCatching { productRepository.deleteProduct(productId) }
                .onSuccess { _editorState.value = null }
                .onFailure {
                    updateForm {
                        copy(errorMessage = "Nie udało się usunąć produktu. Może być używany w posiłku.")
                    }
                }
        }
    }

    fun saveProduct() {
        val state = editorState.value ?: return
        val product = state.toProductOrNull()

        if (product == null) {
            updateForm { copy(errorMessage = "Uzupełnij nazwę i podaj poprawne wartości odżywcze.") }
            return
        }

        viewModelScope.launch {
            updateForm { copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (state.editingProductId == null) {
                    productRepository.addProduct(product)
                } else {
                    productRepository.updateProduct(product)
                }
            }
                .onSuccess { _editorState.value = null }
                .onFailure {
                    updateForm {
                        copy(
                            isSaving = false,
                            errorMessage = "Nie udało się zapisać produktu. Sprawdź, czy nazwa nie jest już użyta.",
                        )
                    }
                }
        }
    }

    private fun updateForm(update: ProductFormState.() -> ProductFormState) {
        _editorState.update { state -> state?.update() }
    }
}

data class ProductFormState(
    val editingProductId: Long? = null,
    val name: String = "",
    val calories: String = "",
    val protein: String = "",
    val fat: String = "",
    val carbs: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditing: Boolean
        get() = editingProductId != null
}

private fun ProductFormState.toProductOrNull(): Product? {
    val parsedCalories = calories.toDoubleOrNull()
    val parsedProtein = protein.toDoubleOrNull()
    val parsedFat = fat.toDoubleOrNull()
    val parsedCarbs = carbs.toDoubleOrNull()

    if (
        name.isBlank() ||
        parsedCalories == null ||
        parsedProtein == null ||
        parsedFat == null ||
        parsedCarbs == null ||
        parsedCalories < 0.0 ||
        parsedProtein < 0.0 ||
        parsedFat < 0.0 ||
        parsedCarbs < 0.0
    ) {
        return null
    }

    return Product(
        id = editingProductId ?: 0,
        name = name.trim(),
        caloriesPer100g = parsedCalories,
        proteinPer100g = parsedProtein,
        fatPer100g = parsedFat,
        carbsPer100g = parsedCarbs,
    )
}

private fun Double.formatInput(): String =
    if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }

enum class ProductSortBy(val label: String) {
    Name("Nazwa"),
    Calories("kcal"),
    Protein("Białko"),
    Fat("Tłuszcz"),
    Carbs("Węgle"),
}
