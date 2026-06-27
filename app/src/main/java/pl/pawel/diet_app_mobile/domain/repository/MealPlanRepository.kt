package pl.pawel.diet_app_mobile.domain.repository

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.ImportWeekResult
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.WeekShareSlot

interface MealPlanRepository {
    fun observeWeekPlan(weekStartDate: LocalDate): Flow<MealPlan>

    suspend fun addPlannedMeal(
        weekStartDate: LocalDate,
        mealId: Long,
        date: LocalDate,
        mealType: String,
        servings: Double,
    ): Long

    suspend fun updatePlannedMealServings(plannedMealId: Long, servings: Double)

    suspend fun replacePlannedMeal(plannedMealId: Long, newMealId: Long, servings: Double)

    suspend fun removePlannedMeal(plannedMealId: Long)

    /**
     * Oznacza wszystkie posiłki w slocie ([date] + [mealType]) jako „jem na mieście"
     * (pomijane w liście zakupów i w sumie kalorii) albo cofa to oznaczenie.
     */
    suspend fun setSlotSkipped(date: LocalDate, mealType: String, skipped: Boolean)

    /**
     * Zwraca sąsiednie dni (spośród [date] − 1 oraz [date] + 1), w których w dowolnym slocie
     * zaplanowano posiłek o id [mealId]. Działa również przez granicę tygodnia.
     */
    suspend fun adjacentDaysWithSameMeal(mealId: Long, date: LocalDate): List<LocalDate>

    /**
     * Zastępuje plan tygodnia [weekStartDate] slotami z udostępnionego planu. Posiłki są
     * dopasowywane po nazwie; nieznane lokalnie są pomijane i zwracane w wyniku.
     */
    suspend fun applySharedWeek(
        weekStartDate: LocalDate,
        slots: List<WeekShareSlot>,
    ): ImportWeekResult

    /**
     * Copies all planned meals from [sourceDate] in [mealType] to [targetDate] under [targetWeekStartDate].
     * Returns number of meals copied (0 if source slot was empty).
     */
    suspend fun copyDayCategory(
        sourceDate: LocalDate,
        targetDate: LocalDate,
        targetWeekStartDate: LocalDate,
        mealType: String,
    ): Int
}
