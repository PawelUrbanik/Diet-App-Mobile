package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planned_meals",
    foreignKeys = [
        ForeignKey(
            entity = MealPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["mealPlanId"]),
        Index(value = ["mealId"]),
        Index(value = ["date"]),
    ],
)
data class PlannedMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealPlanId: Long,
    val mealId: Long,
    val date: String,
    val mealType: String,
    val servings: Double,
    val position: Int,
    val skipped: Boolean = false,
)
