package pl.pawel.diet_app_mobile.data.local.seed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity

@Singleton
class ProductSeeder @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val productDao: ProductDao,
) {
    suspend fun seedMissingProducts() {
        val now = System.currentTimeMillis()
        val products = context.assets.open(SEED_FILE_NAME)
            .bufferedReader(Charsets.UTF_8)
            .useLines { lines ->
                lines
                    .drop(1)
                    .mapNotNull { line -> line.toProductEntityOrNull(now) }
                    .toList()
            }

        if (products.isNotEmpty()) {
            productDao.insertAll(products)
        }
    }

    private fun String.toProductEntityOrNull(timestamp: Long): ProductEntity? {
        val columns = parseCsvLine()
        if (columns.size != EXPECTED_COLUMN_COUNT) return null

        val name = columns[0].trim()
        val category = columns[1].trim()
        if (name.isBlank()) return null

        return ProductEntity(
            name = name,
            category = category.ifBlank { "Inne" },
            caloriesPer100g = columns[2].toDoubleOrNull() ?: return null,
            proteinPer100g = columns[3].toDoubleOrNull() ?: return null,
            fatPer100g = columns[4].toDoubleOrNull() ?: return null,
            carbsPer100g = columns[5].toDoubleOrNull() ?: return null,
            createdAt = timestamp,
            updatedAt = timestamp,
        )
    }

    private fun String.parseCsvLine(): List<String> {
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
                char == ',' && !insideQuotes -> {
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

    private companion object {
        const val SEED_FILE_NAME = "products_seed.csv"
        const val EXPECTED_COLUMN_COUNT = 6
    }
}
