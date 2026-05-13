package pl.pawel.diet_app_mobile.domain.model

data class MealIngredient(
    val id: Long = 0,
    val mealId: Long = 0,
    val product: Product,
    val quantityGrams: Double,
    val nutrition: NutritionSummary,
)
