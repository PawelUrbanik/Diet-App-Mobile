package pl.pawel.diet_app_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.data.local.entity.MealPlanEntity
import pl.pawel.diet_app_mobile.data.local.entity.PlannedMealEntity

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans WHERE weekStartDate = :weekStartDate LIMIT 1")
    fun observePlanForWeek(weekStartDate: String): Flow<MealPlanEntity?>

    @Query("SELECT * FROM meal_plans WHERE weekStartDate = :weekStartDate LIMIT 1")
    suspend fun getPlanForWeek(weekStartDate: String): MealPlanEntity?

    @Query(
        "SELECT pm.* FROM planned_meals pm " +
            "INNER JOIN meal_plans mp ON mp.id = pm.mealPlanId " +
            "WHERE mp.weekStartDate = :weekStartDate " +
            "ORDER BY pm.date ASC, pm.position ASC",
    )
    fun observePlannedMealsForWeek(weekStartDate: String): Flow<List<PlannedMealEntity>>

    @Query(
        "SELECT COALESCE(MAX(position), -1) FROM planned_meals " +
            "WHERE mealPlanId = :mealPlanId AND date = :date AND mealType = :mealType",
    )
    suspend fun getMaxPositionForSlot(mealPlanId: Long, date: String, mealType: String): Int

    @Query(
        "SELECT * FROM planned_meals WHERE date = :date AND mealType = :mealType " +
            "ORDER BY position ASC",
    )
    suspend fun getPlannedMealsForSlot(date: String, mealType: String): List<PlannedMealEntity>

    @Query(
        "SELECT pm.* FROM planned_meals pm " +
            "INNER JOIN meal_plans mp ON mp.id = pm.mealPlanId " +
            "WHERE mp.weekStartDate = :weekStartDate " +
            "ORDER BY pm.date ASC, pm.position ASC",
    )
    suspend fun getPlannedMealsForWeek(weekStartDate: String): List<PlannedMealEntity>

    @Query(
        "DELETE FROM planned_meals WHERE mealPlanId IN " +
            "(SELECT id FROM meal_plans WHERE weekStartDate = :weekStartDate)",
    )
    suspend fun deletePlannedMealsForWeek(weekStartDate: String)

    @Query(
        "SELECT * FROM planned_meals WHERE date >= :startDate AND date <= :endDate " +
            "ORDER BY date ASC, position ASC",
    )
    suspend fun getPlannedMealsBetween(startDate: String, endDate: String): List<PlannedMealEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlan(plan: MealPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlannedMeal(plannedMeal: PlannedMealEntity): Long

    @Query("UPDATE planned_meals SET servings = :servings WHERE id = :plannedMealId")
    suspend fun updateServings(plannedMealId: Long, servings: Double)

    @Query("UPDATE planned_meals SET mealId = :newMealId, servings = :servings WHERE id = :plannedMealId")
    suspend fun updateMealAndServings(plannedMealId: Long, newMealId: Long, servings: Double)

    @Query("SELECT mealPlanId FROM planned_meals WHERE id = :plannedMealId")
    suspend fun getMealPlanIdFor(plannedMealId: Long): Long?

    @Transaction
    suspend fun replacePlannedMeal(plannedMealId: Long, newMealId: Long, servings: Double) {
        updateMealAndServings(plannedMealId, newMealId, servings)
        val planId = getMealPlanIdFor(plannedMealId) ?: return
        touchPlan(planId, System.currentTimeMillis())
    }

    @Query("UPDATE meal_plans SET updatedAt = :updatedAt WHERE id = :mealPlanId")
    suspend fun touchPlan(mealPlanId: Long, updatedAt: Long)

    @Query("DELETE FROM planned_meals WHERE id = :plannedMealId")
    suspend fun deletePlannedMealById(plannedMealId: Long)

    @Transaction
    suspend fun addPlannedMeal(
        weekStartDate: String,
        mealId: Long,
        date: String,
        mealType: String,
        servings: Double,
    ): Long {
        val now = System.currentTimeMillis()
        val plan = getPlanForWeek(weekStartDate) ?: run {
            val newId = insertPlan(
                MealPlanEntity(
                    weekStartDate = weekStartDate,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
            MealPlanEntity(id = newId, weekStartDate = weekStartDate, createdAt = now, updatedAt = now)
        }
        val nextPosition = getMaxPositionForSlot(plan.id, date, mealType) + 1
        val plannedMealId = insertPlannedMeal(
            PlannedMealEntity(
                mealPlanId = plan.id,
                mealId = mealId,
                date = date,
                mealType = mealType,
                servings = servings,
                position = nextPosition,
            ),
        )
        touchPlan(plan.id, now)
        return plannedMealId
    }
}
