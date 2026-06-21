package pl.pawel.diet_app_mobile.data.repository

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.data.local.dao.WeekTemplateDao
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateSlotEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateWithSlots
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.WeekTemplate
import pl.pawel.diet_app_mobile.domain.model.WeekTemplateSlot
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.domain.repository.WeekTemplateRepository

class RoomWeekTemplateRepository @Inject constructor(
    private val weekTemplateDao: WeekTemplateDao,
    private val mealPlanDao: MealPlanDao,
    private val mealRepository: MealRepository,
) : WeekTemplateRepository {

    override fun observeTemplates(): Flow<List<WeekTemplate>> =
        combine(
            weekTemplateDao.observeAll(),
            mealRepository.observeMeals(),
        ) { userTemplatesWithSlots, meals ->
            val mealsById: Map<Long, Meal> = meals.associateBy { it.id }
            val mealsByName: Map<String, Meal> = meals.associateBy { it.name.normalizedKey() }

            val predefined = PredefinedWeekTemplates.ALL.map { config ->
                config.toDomain(mealsByName)
            }
            val user = userTemplatesWithSlots.map { templateWithSlots ->
                templateWithSlots.toDomain(mealsById)
            }
            predefined + user
        }

    override suspend fun saveCurrentWeekAsTemplate(name: String, weekStartDate: LocalDate): Int {
        val plannedMeals = mealPlanDao.getPlannedMealsForWeek(weekStartDate.format(DATE_FORMATTER))
        if (plannedMeals.isEmpty()) return 0

        val now = System.currentTimeMillis()
        weekTemplateDao.createTemplateWithSlots(
            template = WeekTemplateEntity(name = name.trim(), createdAt = now),
        ) { templateId ->
            plannedMeals.map { entity ->
                val date = LocalDate.parse(entity.date, DATE_FORMATTER)
                val dayOffset = (date.toEpochDay() - weekStartDate.toEpochDay()).toInt()
                    .coerceIn(0, 6)
                WeekTemplateSlotEntity(
                    templateId = templateId,
                    dayOffset = dayOffset,
                    mealType = entity.mealType,
                    mealId = entity.mealId,
                    servings = entity.servings,
                    position = entity.position,
                )
            }
        }
        return plannedMeals.size
    }

    override suspend fun applyTemplate(templateId: String, weekStartDate: LocalDate) {
        val slots = resolveSlotsForApply(templateId) ?: return
        val weekStartString = weekStartDate.format(DATE_FORMATTER)

        mealPlanDao.deletePlannedMealsForWeek(weekStartString)
        slots.forEach { slot ->
            val targetDate = weekStartDate.plusDays(slot.dayOffset.toLong())
            mealPlanDao.addPlannedMeal(
                weekStartDate = weekStartString,
                mealId = slot.mealId,
                date = targetDate.format(DATE_FORMATTER),
                mealType = slot.mealType,
                servings = slot.servings,
            )
        }
    }

    override suspend fun deleteTemplate(templateId: String) {
        val userId = parseUserTemplateId(templateId) ?: return
        weekTemplateDao.deleteTemplate(userId)
    }

    private suspend fun resolveSlotsForApply(templateId: String): List<ResolvedSlot>? {
        if (templateId.startsWith(PREDEFINED_PREFIX)) {
            val config = PredefinedWeekTemplates.ALL.firstOrNull { it.id == templateId }
                ?: return null
            val mealsByName: Map<String, Meal> = mealRepository.observeMeals().first()
                .associateBy { it.name.normalizedKey() }
            return config.rawSlots.mapNotNull { slot ->
                val meal = mealsByName[slot.mealName.normalizedKey()] ?: return@mapNotNull null
                ResolvedSlot(slot.dayOffset, slot.mealType, meal.id, slot.servings)
            }
        }
        val userId = parseUserTemplateId(templateId) ?: return null
        val template = weekTemplateDao.observeAll().first()
            .firstOrNull { it.template.id == userId }
            ?: return null
        return template.slots.map {
            ResolvedSlot(it.dayOffset, it.mealType, it.mealId, it.servings)
        }
    }

    private data class ResolvedSlot(
        val dayOffset: Int,
        val mealType: String,
        val mealId: Long,
        val servings: Double,
    )

    private companion object {
        const val PREDEFINED_PREFIX = "predef:"
        const val USER_PREFIX = "user:"
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun parseUserTemplateId(templateId: String): Long? =
            templateId.removePrefix(USER_PREFIX).takeIf { it != templateId }?.toLongOrNull()
    }
}

private fun PredefinedTemplateConfig.toDomain(
    mealsByName: Map<String, pl.pawel.diet_app_mobile.domain.model.Meal>,
): WeekTemplate {
    val slots = rawSlots.mapNotNull { raw ->
        val meal = mealsByName[raw.mealName.normalizedKey()] ?: return@mapNotNull null
        WeekTemplateSlot(
            dayOffset = raw.dayOffset,
            mealType = raw.mealType,
            meal = meal,
            servings = raw.servings,
            position = 0,
        )
    }
    return WeekTemplate(
        id = id,
        name = name,
        isPredefined = true,
        slots = slots,
    )
}

private fun WeekTemplateWithSlots.toDomain(
    mealsById: Map<Long, pl.pawel.diet_app_mobile.domain.model.Meal>,
): WeekTemplate {
    val resolvedSlots = slots.mapNotNull { entity ->
        val meal = mealsById[entity.mealId] ?: return@mapNotNull null
        WeekTemplateSlot(
            dayOffset = entity.dayOffset,
            mealType = entity.mealType,
            meal = meal,
            servings = entity.servings,
            position = entity.position,
        )
    }
    return WeekTemplate(
        id = "user:${template.id}",
        name = template.name,
        isPredefined = false,
        slots = resolvedSlots,
    )
}

private fun String.normalizedKey(): String = trim().lowercase()
