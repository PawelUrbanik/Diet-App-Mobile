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
                        if (mealKey in importedMealNames) {
                            // Posiłek już zaimportowany — uzupełnij kategorię, jeśli wciąż
                            // siedzi na wartości domyślnej (np. zaseedowany zanim CSV miał
                            // kategorie). Ręcznie zmienione kategorie pozostają nietknięte.
                            if (seedMeal.meal.category != DEFAULT_MEAL_CATEGORY) {
                                mealDao.reassignCategoryByName(
                                    name = seedMeal.meal.name,
                                    previousCategory = DEFAULT_MEAL_CATEGORY,
                                    newCategory = seedMeal.meal.category,
                                )
                            }
                            return@forEach
                        }

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

        val category = columns.getOrNull(8)?.trim()?.takeIf { it.isNotBlank() }
            ?: DEFAULT_MEAL_CATEGORY

        return SeedMeal(
            meal = MealEntity(
                name = mealName,
                description = buildDescription(columns),
                category = category,
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
            "Brokuły" to "Brokuł świeży",
            "Brokuły, mrożone" to "Brokuły mrożone",
            "Sałata" to "Sałata masłowa",
            "Maliny" to "Malina",
            "Ogórek kwaszony" to "Ogórek kiszony",
            "Papryka czerwona" to "Papryka czerwona słodka",
            "Pietruszka, liście" to "Pietruszka",
            "Pomidor" to "Pomidor czerwony",
            "Żurawina suszona" to "Suszona żurawina",
            "Jaja kurze całe" to "Jajko",
            "Mleko spożywcze, 2% tłuszczu" to "Mleko 2%",
            "Ser twarogowy chudy" to "Twaróg chudy",
            "Ser, edamski tłusty" to "Ser edamski",
            "Ser, mozzarella" to "Ser mozzarella",
            "Serek wiejski (light)" to "Serek wiejski lekki",
            "Skyr jogurt naturalny" to "Skyr",
            "Śmietana, 12% tłuszczu" to "Śmietana 12%",
            "Mięso z piersi kurczaka, bez skóry" to "Pierś z kurczaka",
            "Chude mięso mielone z szynki wieprzowej" to "Mięso mielone z szynki",
            "Mielony filet z piersi indyka (bez skóry)" to "Mielony filet z indyka",
            "Polędwica wieprzowa (surowa)" to "Polędwica wieprzowa",
            "Wieprzowina, schab surowy bez kości" to "Schab wieprzowy",
            "Szynka z piersi kurczaka" to "Szynka drobiowa",
            "Mąka pszenna, typ 1850" to "Mąka pszenna typ 1850",
            "Mąka pszenna, typ 500" to "Mąka pszenna",
            "Dynia, pestki, łuskane" to "Pestki dyni",
            "Kakao niskotłuszczowe" to "Kakao w proszku odtłuszczone",
            "Masło orzechowe" to "Pasta orzechowa",
            "Siemię lniane (świeżo mielone)" to "Siemię lniane",
            "Makrela, wędzona" to "Makrela wędzona",
            "Łosoś, wędzony" to "Łosoś wędzony",
            "Łosoś, świeży" to "Łosoś świeży",
            "Chleb żytni razowy" to "Chleb żytni",
            "Bulion warzywny (domowy)" to "Bulion warzywny",
            "Dżem truskawkowy, niskosłodzony" to "Dżem truskawkowy",
            "Miód pszczeli" to "Miód",
            "Odżywka KFD" to "Odżywka białkowa",
            "Passata pomidorowa (przecier)" to "Passata pomidorowa",
            "Pesto zielone (wegańskie)" to "Pesto zielone",
            "Bazylia (suszona)" to "Bazylia",
            "Mielona słodka papryka" to "Papryka mielona",
            "Papryka słodka (mielona, wędzona)" to "Papryka mielona",
            "Pieprz czarny mielony" to "Pieprz czarny",
            "Sól biała" to "Sól",
            "Sól himalajska" to "Sól",
            "Tymianek" to "Tymianek suszony",
            "Kefir (1.5% tł.)" to "Kefir 1.5%",
            "Trio warzywne Mroźna Kraina (Biedronka)" to "Mieszanka warzywna mrożona",
            "Truskawki, mrożone" to "Truskawki mrożone",
            "Koper (w pęczkach)" to "Koper ogrodowy",
        )
    }
}
