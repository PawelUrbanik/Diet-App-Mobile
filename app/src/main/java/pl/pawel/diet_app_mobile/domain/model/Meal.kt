package pl.pawel.diet_app_mobile.domain.model

data class Meal(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val category: String,
    val ingredients: List<MealIngredient> = emptyList(),
    val nutrition: NutritionSummary = NutritionSummary(),
)
