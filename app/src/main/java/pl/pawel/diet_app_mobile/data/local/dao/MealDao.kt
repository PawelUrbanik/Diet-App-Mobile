package pl.pawel.diet_app_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.data.local.entity.MealEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealIngredientEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealWithIngredients

@Dao
interface MealDao {
    @Transaction
    @Query("SELECT * FROM meals ORDER BY name COLLATE NOCASE ASC")
    fun observeMealsWithIngredients(): Flow<List<MealWithIngredients>>

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun observeMealWithIngredients(mealId: Long): Flow<MealWithIngredients?>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Long): MealEntity?

    @Query("SELECT name FROM meals")
    suspend fun getMealNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query(
        "UPDATE meals SET category = :newCategory " +
            "WHERE name = :name AND category = :previousCategory",
    )
    suspend fun reassignCategoryByName(name: String, previousCategory: String, newCategory: String)

    @Query(
        "UPDATE meals SET description = :description WHERE name = :name AND " +
            "(description IS NULL OR description LIKE 'Porcje z importu%' OR " +
            "description LIKE 'Czas przygotowania%')",
    )
    suspend fun fillSeedDescriptionByName(name: String, description: String)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIngredient(ingredient: MealIngredientEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIngredients(ingredients: List<MealIngredientEntity>)

    @Query("DELETE FROM meal_ingredients WHERE mealId = :mealId")
    suspend fun deleteIngredientsForMeal(mealId: Long)

    @Transaction
    suspend fun insertMealWithIngredients(
        meal: MealEntity,
        ingredients: List<MealIngredientEntity>,
    ): Long {
        val mealId = insertMeal(meal)
        if (ingredients.isNotEmpty()) {
            insertIngredients(
                ingredients.map { ingredient -> ingredient.copy(mealId = mealId) },
            )
        }
        return mealId
    }

    @Transaction
    suspend fun replaceMealIngredients(
        mealId: Long,
        ingredients: List<MealIngredientEntity>,
    ) {
        deleteIngredientsForMeal(mealId)
        if (ingredients.isNotEmpty()) {
            insertIngredients(
                ingredients.map { ingredient -> ingredient.copy(mealId = mealId) },
            )
        }
    }
}
