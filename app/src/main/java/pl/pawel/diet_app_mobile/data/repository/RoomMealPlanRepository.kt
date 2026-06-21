package pl.pawel.diet_app_mobile.data.repository

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.domain.model.DayPlan
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealPlan
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.domain.repository.MealPlanRepository
import pl.pawel.diet_app_mobile.domain.repository.MealRepository

class RoomMealPlanRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val mealRepository: MealRepository,
) : MealPlanRepository {
    override fun observeWeekPlan(weekStartDate: LocalDate): Flow<MealPlan> {
        val weekString = weekStartDate.format(DATE_FORMATTER)
        return combine(
            mealPlanDao.observePlanForWeek(weekString),
            mealPlanDao.observePlannedMealsForWeek(weekString),
            mealRepository.observeMeals(),
        ) { planEntity, plannedMealEntities, meals ->
            val mealsById: Map<Long, Meal> = meals.associateBy { it.id }
            val planId = planEntity?.id ?: 0L

            val plannedMeals = plannedMealEntities.mapNotNull { entity ->
                val meal = mealsById[entity.mealId] ?: return@mapNotNull null
                PlannedMeal(
                    id = entity.id,
                    mealPlanId = entity.mealPlanId,
                    meal = meal,
                    date = LocalDate.parse(entity.date, DATE_FORMATTER),
                    mealType = entity.mealType,
                    servings = entity.servings,
                    position = entity.position,
                )
            }

            val days = (0L..6L).map { offset ->
                val date = weekStartDate.plusDays(offset)
                DayPlan(
                    date = date,
                    plannedMeals = plannedMeals
                        .filter { it.date == date }
                        .sortedBy { it.position },
                )
            }

            MealPlan(
                id = planId,
                weekStartDate = weekStartDate,
                days = days,
            )
        }
    }

    override suspend fun addPlannedMeal(
        weekStartDate: LocalDate,
        mealId: Long,
        date: LocalDate,
        mealType: String,
        servings: Double,
    ): Long = mealPlanDao.addPlannedMeal(
        weekStartDate = weekStartDate.format(DATE_FORMATTER),
        mealId = mealId,
        date = date.format(DATE_FORMATTER),
        mealType = mealType,
        servings = servings,
    )

    override suspend fun updatePlannedMealServings(plannedMealId: Long, servings: Double) {
        mealPlanDao.updateServings(plannedMealId, servings)
    }

    override suspend fun replacePlannedMeal(plannedMealId: Long, newMealId: Long, servings: Double) {
        mealPlanDao.replacePlannedMeal(plannedMealId, newMealId, servings)
    }

    override suspend fun removePlannedMeal(plannedMealId: Long) {
        mealPlanDao.deletePlannedMealById(plannedMealId)
    }

    override suspend fun adjacentDaysWithSameMeal(mealId: Long, date: LocalDate): List<LocalDate> {
        val previous = date.minusDays(1)
        val next = date.plusDays(1)
        return mealPlanDao.getPlannedMealsBetween(
            startDate = previous.format(DATE_FORMATTER),
            endDate = next.format(DATE_FORMATTER),
        )
            .asSequence()
            .filter { it.mealId == mealId }
            .map { LocalDate.parse(it.date, DATE_FORMATTER) }
            .filter { it == previous || it == next }
            .distinct()
            .sorted()
            .toList()
    }

    override suspend fun copyDayCategory(
        sourceDate: LocalDate,
        targetDate: LocalDate,
        targetWeekStartDate: LocalDate,
        mealType: String,
    ): Int {
        val sourceMeals = mealPlanDao.getPlannedMealsForSlot(
            date = sourceDate.format(DATE_FORMATTER),
            mealType = mealType,
        )
        sourceMeals.forEach { source ->
            mealPlanDao.addPlannedMeal(
                weekStartDate = targetWeekStartDate.format(DATE_FORMATTER),
                mealId = source.mealId,
                date = targetDate.format(DATE_FORMATTER),
                mealType = mealType,
                servings = source.servings,
            )
        }
        return sourceMeals.size
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
