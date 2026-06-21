package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "week_template_slots",
    foreignKeys = [
        ForeignKey(
            entity = WeekTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["templateId"]),
        Index(value = ["mealId"]),
    ],
)
data class WeekTemplateSlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long,
    val dayOffset: Int,
    val mealType: String,
    val mealId: Long,
    val servings: Double,
    val position: Int,
)
