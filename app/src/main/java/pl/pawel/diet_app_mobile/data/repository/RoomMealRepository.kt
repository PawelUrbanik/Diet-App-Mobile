package pl.pawel.diet_app_mobile.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.pawel.diet_app_mobile.data.local.dao.MealDao
import pl.pawel.diet_app_mobile.data.local.entity.IngredientWithProduct
import pl.pawel.diet_app_mobile.data.local.entity.MealEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealIngredientEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealWithIngredients
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealIngredient
import pl.pawel.diet_app_mobile.domain.model.NutritionSummary
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.domain.model.nutritionForGrams
import pl.pawel.diet_app_mobile.domain.repository.MealRepository

class RoomMealRepository @Inject constructor(
    private val mealDao: MealDao,
) : MealRepository {
    override fun observeMeals(): Flow<List<Meal>> =
        mealDao.observeMealsWithIngredients().map { meals ->
            meals.map(MealWithIngredients::toDomain)
        }

    override fun observeMeal(mealId: Long): Flow<Meal?> =
        mealDao.observeMealWithIngredients(mealId).map { meal ->
            meal?.toDomain()
        }

    override suspend fun addMeal(meal: Meal): Long {
        val now = System.currentTimeMillis()
        return mealDao.insertMealWithIngredients(
            meal = meal.toEntity(createdAt = now, updatedAt = now),
            ingredients = meal.ingredients.map(MealIngredient::toEntity),
        )
    }

    override suspend fun updateMeal(meal: Meal) {
        val existingMeal = mealDao.getMealById(meal.id) ?: return
        mealDao.updateMeal(
            meal.toEntity(
                createdAt = existingMeal.createdAt,
                updatedAt = System.currentTimeMillis(),
            ),
        )
        mealDao.replaceMealIngredients(
            mealId = meal.id,
            ingredients = meal.ingredients.map(MealIngredient::toEntity),
        )
    }

    override suspend fun deleteMeal(mealId: Long) {
        mealDao.deleteMealById(mealId)
    }
}

private fun MealWithIngredients.toDomain(): Meal {
    val mappedIngredients = ingredients.map(IngredientWithProduct::toDomain)
    return Meal(
        id = meal.id,
        name = meal.name,
        description = meal.description,
        category = meal.category,
        ingredients = mappedIngredients,
        nutrition = mappedIngredients.fold(NutritionSummary()) { total, ingredient ->
            total + ingredient.nutrition
        },
    )
}

private fun IngredientWithProduct.toDomain(): MealIngredient {
    val mappedProduct = product.toDomain()

    return MealIngredient(
        id = ingredient.id,
        mealId = ingredient.mealId,
        product = mappedProduct,
        quantityGrams = ingredient.quantityGrams,
        nutrition = mappedProduct.nutritionForGrams(ingredient.quantityGrams),
    )
}

private fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    category = category,
    caloriesPer100g = caloriesPer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    carbsPer100g = carbsPer100g,
)

private fun Meal.toEntity(
    createdAt: Long,
    updatedAt: Long,
): MealEntity = MealEntity(
    id = id,
    name = name.trim(),
    description = description?.trim()?.ifBlank { null },
    category = category.trim(),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun MealIngredient.toEntity(): MealIngredientEntity = MealIngredientEntity(
    id = id,
    mealId = mealId,
    productId = product.id,
    quantityGrams = quantityGrams,
)
