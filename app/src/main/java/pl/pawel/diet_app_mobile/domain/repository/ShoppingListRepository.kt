package pl.pawel.diet_app_mobile.domain.repository

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.ShoppingListItem

interface ShoppingListRepository {
    fun observeItems(): Flow<List<ShoppingListItem>>

    /**
     * Generates shopping list entries from planned meals in the inclusive date range
     * [startDate]..[endDate]. Aggregates ingredient grams per product across all planned
     * meals (quantity × servings). Existing generated entries are merged: quantities are
     * refreshed, the "checked" state is preserved for products still present, generated
     * entries no longer needed are removed, and manually added entries are left untouched.
     *
     * Returns the number of distinct product lines after generation.
     */
    suspend fun generateFromPlan(startDate: LocalDate, endDate: LocalDate): Int

    suspend fun setChecked(itemId: Long, isChecked: Boolean)

    suspend fun addManualItem(name: String, category: String)

    suspend fun removeItem(itemId: Long)

    suspend fun clearChecked()

    suspend fun clearAll()
}
