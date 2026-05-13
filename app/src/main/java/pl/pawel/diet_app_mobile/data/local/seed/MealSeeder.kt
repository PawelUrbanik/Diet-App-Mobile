package pl.pawel.diet_app_mobile.data.local.seed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import pl.pawel.diet_app_mobile.data.local.dao.MealDao
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.entity.MealEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealIngredientEntity
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity

@Singleton
class MealSeeder @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val mealDao: MealDao,
    private val productDao: ProductDao,
    private val productSeeder: ProductSeeder,
) {
    suspend fun seedMissingMeals() {
        productSeeder.seedMissingProducts()

        val importedMealNames = mealDao.getMealNames()
            .map { name -> name.normalizedKey() }
            .toMutableSet()
        val productsByName = productDao.getAll()
            .associateBy { product -> product.name.normalizedKey() }
        val now = System.currentTimeMillis()

        context.assets.open(SEED_FILE_NAME)
            .bufferedReader(Charsets.UTF_8)
            .useLines { lines ->
                lines
                    .drop(1)
                    .mapNotNull { line -> line.toSeedMealOrNull(productsByName, now) }
                    .forEach { seedMeal ->
                        val mealKey = seedMeal.meal.name.normalizedKey()
                        if (mealKey in importedMealNames) return@forEach

                        mealDao.insertMealWithIngredients(
                            meal = seedMeal.meal,
                            ingredients = seedMeal.ingredients,
                        )
                        importedMealNames.add(mealKey)
                    }
            }
    }

    private fun String.toSeedMealOrNull(
        productsByName: Map<String, ProductEntity>,
        timestamp: Long,
    ): SeedMeal? {
        val columns = parseSeparatedLine(separator = ';')
        if (columns.size < EXPECTED_COLUMN_COUNT) return null

        val mealName = columns[0].trim()
        if (mealName.isBlank()) return null

        val ingredients = columns[7]
            .parseIngredients(productsByName)
            .filter { ingredient -> ingredient.quantityGrams > 0.0 }

        return SeedMeal(
            meal = MealEntity(
                name = mealName,
                description = buildDescription(columns),
                category = DEFAULT_MEAL_CATEGORY,
                createdAt = timestamp,
                updatedAt = timestamp,
            ),
            ingredients = ingredients,
        )
    }

    private fun String.parseIngredients(
        productsByName: Map<String, ProductEntity>,
    ): List<MealIngredientEntity> = split(';')
        .mapNotNull { rawIngredient ->
            val parts = rawIngredient.split(':', limit = 2)
            if (parts.size != 2) return@mapNotNull null

            val productName = parts[0].trim().canonicalProductName()
            val product = productsByName[productName.normalizedKey()] ?: return@mapNotNull null
            val quantityGrams = parts[1].trim().toGramsOrNull() ?: return@mapNotNull null

            MealIngredientEntity(
                mealId = 0,
                productId = product.id,
                quantityGrams = quantityGrams,
            )
        }

    private fun buildDescription(columns: List<String>): String? {
        val portions = columns[1].trim()
        val preparationMinutes = columns[2].trim()
        return buildList {
            if (portions.isNotBlank()) add("Porcje z importu: $portions")
            if (preparationMinutes.isNotBlank()) add("Czas przygotowania: $preparationMinutes min")
        }.joinToString(separator = "\n").ifBlank { null }
    }

    private fun String.toGramsOrNull(): Double? {
        val normalized = replace(',', '.')
            .replace("kg", "")
            .trim()
        val kilograms = normalized.toDoubleOrNull() ?: return null
        return kilograms * GRAMS_IN_KILOGRAM
    }

    private fun String.canonicalProductName(): String = PRODUCT_ALIASES[this] ?: this

    private fun String.normalizedKey(): String = trim().lowercase()

    private fun String.parseSeparatedLine(separator: Char): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var index = 0
        var insideQuotes = false

        while (index < length) {
            val char = this[index]
            when {
                char == '"' && insideQuotes && getOrNull(index + 1) == '"' -> {
                    current.append('"')
                    index++
                }

                char == '"' -> insideQuotes = !insideQuotes
                char == separator && !insideQuotes -> {
                    result.add(current.toString())
                    current.clear()
                }

                else -> current.append(char)
            }
            index++
        }

        result.add(current.toString())
        return result
    }

    private data class SeedMeal(
        val meal: MealEntity,
        val ingredients: List<MealIngredientEntity>,
    )

    private companion object {
        const val SEED_FILE_NAME = "meals_seed.csv"
        const val EXPECTED_COLUMN_COUNT = 8
        const val DEFAULT_MEAL_CATEGORY = "Obiad"
        const val GRAMS_IN_KILOGRAM = 1000.0

        val PRODUCT_ALIASES = mapOf(
            "Czosnek, surowy" to "Czosnek surowy",
            "Mąka pszenna pełnozbożowa" to "Mąka pszenna pełnoziarnista",
            "Ser parmesan tarty" to "Parmezan",
        )
    }
}
