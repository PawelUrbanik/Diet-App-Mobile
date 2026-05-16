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

    suspend fun removePlannedMeal(plannedMealId: Long)
}
