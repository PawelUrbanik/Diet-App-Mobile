package pl.pawel.diet_app_mobile.data.repository

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.data.local.dao.ShoppingListDao
import pl.pawel.diet_app_mobile.data.local.entity.ShoppingListItemEntity
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.PlannedMealSummary
import pl.pawel.diet_app_mobile.domain.model.ShoppingListItem
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.domain.repository.ShoppingListRepository

class RoomShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao,
    private val mealPlanDao: MealPlanDao,
    private val mealRepository: MealRepository,
) : ShoppingListRepository {
    override fun observeItems(): Flow<List<ShoppingListItem>> =
        shoppingListDao.observeAll().map { items -> items.map(ShoppingListItemEntity::toDomain) }

    override suspend fun mealsInRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<PlannedMealSummary> {
        val plannedMeals = mealPlanDao.getPlannedMealsBetween(
            startDate = startDate.format(DATE_FORMATTER),
            endDate = endDate.format(DATE_FORMATTER),
        )
        if (plannedMeals.isEmpty()) return emptyList()

        val occurrences: Map<Long, Int> = plannedMeals
            .filterNot { it.skipped }
            .groupingBy { it.mealId }
            .eachCount()
        return occurrences.keys
            .mapNotNull { mealId -> mealRepository.observeMeal(mealId).first() }
            .map { meal -> PlannedMealSummary(meal.id, meal.name, occurrences[meal.id] ?: 0) }
            .sortedBy { it.name }
    }

    override suspend fun generateFromPlan(
        startDate: LocalDate,
        endDate: LocalDate,
        excludedMealIds: Set<Long>,
    ): Int {
        val plannedMeals = mealPlanDao.getPlannedMealsBetween(
            startDate = startDate.format(DATE_FORMATTER),
            endDate = endDate.format(DATE_FORMATTER),
        )

        val mealsById: Map<Long, Meal> = plannedMeals
            .map { it.mealId }
            .distinct()
            .mapNotNull { mealId -> mealRepository.observeMeal(mealId).first() }
            .associateBy { it.id }

        val aggregated = LinkedHashMap<Long, AggregatedLine>()
        plannedMeals.forEach { plannedMeal ->
            if (plannedMeal.skipped) return@forEach
            if (plannedMeal.mealId in excludedMealIds) return@forEach
            val meal = mealsById[plannedMeal.mealId] ?: return@forEach
            meal.ingredients.forEach { ingredient ->
                val grams = ingredient.quantityGrams * plannedMeal.servings
                val line = aggregated.getOrPut(ingredient.product.id) {
                    AggregatedLine(
                        productId = ingredient.product.id,
                        name = ingredient.product.name,
                        category = ingredient.product.category,
                    )
                }
                line.quantityGrams += grams
            }
        }

        val items = aggregated.values.map { line ->
            ShoppingListItemEntity(
                productId = line.productId,
                name = line.name,
                category = line.category,
                quantityGrams = line.quantityGrams,
                isChecked = false,
                isManual = false,
                createdAt = 0L,
                updatedAt = 0L,
            )
        }

        shoppingListDao.mergeGenerated(items)
        return items.size
    }

    override suspend fun setChecked(itemId: Long, isChecked: Boolean) {
        shoppingListDao.setChecked(itemId, isChecked, System.currentTimeMillis())
    }

    override suspend fun addManualItem(name: String, category: String) {
        val now = System.currentTimeMillis()
        shoppingListDao.insert(
            ShoppingListItemEntity(
                productId = null,
                name = name.trim(),
                category = category.trim().ifBlank { "Inne" },
                quantityGrams = 0.0,
                isChecked = false,
                isManual = true,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun removeItem(itemId: Long) {
        shoppingListDao.deleteById(itemId)
    }

    override suspend fun clearChecked() {
        shoppingListDao.deleteChecked()
    }

    override suspend fun clearAll() {
        shoppingListDao.deleteAll()
    }

    private class AggregatedLine(
        val productId: Long,
        val name: String,
        val category: String,
        var quantityGrams: Double = 0.0,
    )

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}

private fun ShoppingListItemEntity.toDomain(): ShoppingListItem = ShoppingListItem(
    id = id,
    productId = productId,
    name = name,
    category = category,
    quantityGrams = quantityGrams,
    isChecked = isChecked,
    isManual = isManual,
)
