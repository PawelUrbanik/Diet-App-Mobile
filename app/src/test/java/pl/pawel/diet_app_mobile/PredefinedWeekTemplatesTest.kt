package pl.pawel.diet_app_mobile

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.pawel.diet_app_mobile.data.repository.PredefinedWeekTemplates

class PredefinedWeekTemplatesTest {

    @Test
    fun everyTemplateSlotReferencesSeededMeal() {
        val seededMealNames = File("src/main/assets/meals_seed.csv")
            .readLines(Charsets.UTF_8)
            .drop(1)
            .map { line -> line.substringBefore(';').trim().lowercase() }
            .toSet()

        val missing = PredefinedWeekTemplates.ALL.flatMap { template ->
            template.rawSlots
                .filter { slot -> slot.mealName.trim().lowercase() !in seededMealNames }
                .map { slot -> "${template.name}: ${slot.mealName}" }
        }

        assertTrue("Brak posiłków w meals_seed.csv:\n${missing.joinToString("\n")}", missing.isEmpty())
    }
}
