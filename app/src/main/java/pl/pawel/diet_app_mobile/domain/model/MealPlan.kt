package pl.pawel.diet_app_mobile.domain.model

import java.time.LocalDate

data class MealPlan(
    val id: Long = 0,
    val weekStartDate: LocalDate,
    val days: List<DayPlan>,
)

data class DayPlan(
    val date: LocalDate,
    val plannedMeals: List<PlannedMeal>,
) {
    val nutrition: NutritionSummary
        get() = plannedMeals.fold(NutritionSummary()) { acc, planned ->
            acc + planned.nutrition
        }

    fun mealsForCategory(category: String): List<PlannedMeal> =
        plannedMeals.filter { it.mealType == category }.sortedBy { it.position }
}

data class PlannedMeal(
    val id: Long = 0,
    val mealPlanId: Long,
    val meal: Meal,
    val date: LocalDate,
    val mealType: String,
    val servings: Double,
    val position: Int,
) {
    val nutrition: NutritionSummary
        get() = meal.nutrition.times(servings)
}
