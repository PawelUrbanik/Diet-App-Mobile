package pl.pawel.diet_app_mobile.domain.model

data class ShoppingListItem(
    val id: Long = 0,
    val productId: Long?,
    val name: String,
    val category: String,
    val quantityGrams: Double,
    val isChecked: Boolean,
    val isManual: Boolean,
)
