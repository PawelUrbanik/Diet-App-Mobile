package pl.pawel.diet_app_mobile.domain.model

data class NutritionSummary(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
) {
    operator fun plus(other: NutritionSummary): NutritionSummary = NutritionSummary(
        calories = calories + other.calories,
        protein = protein + other.protein,
        fat = fat + other.fat,
        carbs = carbs + other.carbs,
    )

    fun times(multiplier: Double): NutritionSummary = NutritionSummary(
        calories = calories * multiplier,
        protein = protein * multiplier,
        fat = fat * multiplier,
        carbs = carbs * multiplier,
    )
}

fun Product.nutritionForGrams(quantityGrams: Double): NutritionSummary = NutritionSummary(
    calories = caloriesPer100g,
    protein = proteinPer100g,
    fat = fatPer100g,
    carbs = carbsPer100g,
).times(quantityGrams / 100.0)
