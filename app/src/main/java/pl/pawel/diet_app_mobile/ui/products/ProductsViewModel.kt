package pl.pawel.diet_app_mobile.ui.products

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
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.domain.repository.ProductRepository

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    val products: StateFlow<List<Product>> = productRepository.observeProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _formState = MutableStateFlow(ProductFormState())
    val formState: StateFlow<ProductFormState> = _formState.asStateFlow()

    fun onNameChange(value: String) = updateForm { copy(name = value, errorMessage = null) }

    fun onCaloriesChange(value: String) = updateForm { copy(calories = value, errorMessage = null) }

    fun onProteinChange(value: String) = updateForm { copy(protein = value, errorMessage = null) }

    fun onFatChange(value: String) = updateForm { copy(fat = value, errorMessage = null) }

    fun onCarbsChange(value: String) = updateForm { copy(carbs = value, errorMessage = null) }

    fun addProduct() {
        val state = formState.value
        val product = state.toProductOrNull()

        if (product == null) {
            updateForm { copy(errorMessage = "Uzupełnij nazwę i podaj poprawne wartości odżywcze.") }
            return
        }

        viewModelScope.launch {
            updateForm { copy(isSaving = true, errorMessage = null) }
            runCatching { productRepository.addProduct(product) }
                .onSuccess { _formState.value = ProductFormState() }
                .onFailure {
                    updateForm {
                        copy(
                            isSaving = false,
                            errorMessage = "Nie udało się dodać produktu. Sprawdź, czy nazwa nie jest już użyta.",
                        )
                    }
                }
        }
    }

    private fun updateForm(update: ProductFormState.() -> ProductFormState) {
        _formState.update(update)
    }
}

data class ProductFormState(
    val name: String = "",
    val calories: String = "",
    val protein: String = "",
    val fat: String = "",
    val carbs: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

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
        name = name.trim(),
        caloriesPer100g = parsedCalories,
        proteinPer100g = parsedProtein,
        fatPer100g = parsedFat,
        carbsPer100g = parsedCarbs,
    )
}
