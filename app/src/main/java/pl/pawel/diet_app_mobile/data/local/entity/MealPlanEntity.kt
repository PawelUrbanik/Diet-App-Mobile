package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_plans",
    indices = [
        Index(value = ["weekStartDate"], unique = true),
    ],
)
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekStartDate: String,
    val createdAt: Long,
    val updatedAt: Long,
)
