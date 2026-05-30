package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["productId"]),
    ],
)
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long?,
    val name: String,
    val category: String,
    val quantityGrams: Double,
    val isChecked: Boolean,
    val isManual: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
