package pl.pawel.diet_app_mobile.domain.repository

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.MealPlan

interface MealPlanRepository {
    fun observeWeekPlan(weekStartDate: LocalDate): Flow<MealPlan>

    suspend fun addPlannedMeal(
        weekStartDate: LocalDate,
        mealId: Long,
        date: LocalDate,
        mealType: String,
        servings: Double,
    ): Long

    suspend fun updatePlannedMealServings(plannedMealId: Long, servings: Double)

    suspend fun replacePlannedMeal(plannedMealId: Long, newMealId: Long, servings: Double)

    suspend fun removePlannedMeal(plannedMealId: Long)

    /**
     * Zwraca sąsiednie dni (spośród [date] − 1 oraz [date] + 1), w których w dowolnym slocie
     * zaplanowano posiłek o id [mealId]. Działa również przez granicę tygodnia.
     */
    suspend fun adjacentDaysWithSameMeal(mealId: Long, date: LocalDate): List<LocalDate>

    /**
     * Copies all planned meals from [sourceDate] in [mealType] to [targetDate] under [targetWeekStartDate].
     * Returns number of meals copied (0 if source slot was empty).
     */
    suspend fun copyDayCategory(
        sourceDate: LocalDate,
        targetDate: LocalDate,
        targetWeekStartDate: LocalDate,
        mealType: String,
    ): Int
}
