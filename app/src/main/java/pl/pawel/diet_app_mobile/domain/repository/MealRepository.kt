package pl.pawel.diet_app_mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.Meal

interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>

    fun observeMeal(mealId: Long): Flow<Meal?>

    suspend fun addMeal(meal: Meal): Long

    suspend fun updateMeal(meal: Meal)

    suspend fun deleteMeal(mealId: Long)
}
