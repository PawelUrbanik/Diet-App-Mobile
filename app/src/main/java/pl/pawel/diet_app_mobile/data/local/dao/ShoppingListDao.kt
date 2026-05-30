package pl.pawel.diet_app_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.data.local.entity.ShoppingListItemEntity

@Dao
interface ShoppingListDao {
    @Query(
        "SELECT * FROM shopping_list_items " +
            "ORDER BY isChecked ASC, category COLLATE NOCASE ASC, name COLLATE NOCASE ASC",
    )
    fun observeAll(): Flow<List<ShoppingListItemEntity>>

    @Query("SELECT * FROM shopping_list_items WHERE isManual = 0")
    suspend fun getGeneratedItems(): List<ShoppingListItemEntity>

    @Query("SELECT * FROM shopping_list_items WHERE isManual = 0 AND productId = :productId LIMIT 1")
    suspend fun findGeneratedByProduct(productId: Long): ShoppingListItemEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: ShoppingListItemEntity): Long

    @Update
    suspend fun update(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list_items SET isChecked = :isChecked, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setChecked(id: Long, isChecked: Boolean, updatedAt: Long)

    @Query("DELETE FROM shopping_list_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM shopping_list_items WHERE isChecked = 1")
    suspend fun deleteChecked()

    @Query("DELETE FROM shopping_list_items")
    suspend fun deleteAll()

    /**
     * Merges freshly computed generated [items] into the table:
     * - refreshes quantity/name/category for products that already had a generated line
     *   (keeping their id and checked state),
     * - inserts new product lines,
     * - removes generated lines whose product is no longer present.
     * Manual entries (isManual = 1) are never touched.
     */
    @Transaction
    suspend fun mergeGenerated(items: List<ShoppingListItemEntity>) {
        val now = System.currentTimeMillis()
        val keepProductIds = items.mapNotNull { it.productId }.toSet()

        getGeneratedItems().forEach { existing ->
            if (existing.productId == null || existing.productId !in keepProductIds) {
                deleteById(existing.id)
            }
        }

        items.forEach { newItem ->
            val productId = newItem.productId
            val current = if (productId != null) findGeneratedByProduct(productId) else null
            if (current != null) {
                update(
                    current.copy(
                        name = newItem.name,
                        category = newItem.category,
                        quantityGrams = newItem.quantityGrams,
                        updatedAt = now,
                    ),
                )
            } else {
                insert(newItem.copy(createdAt = now, updatedAt = now))
            }
        }
    }
}
