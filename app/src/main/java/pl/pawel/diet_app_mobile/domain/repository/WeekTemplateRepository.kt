package pl.pawel.diet_app_mobile.domain.repository

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.WeekTemplate

interface WeekTemplateRepository {
    fun observeTemplates(): Flow<List<WeekTemplate>>

    suspend fun saveCurrentWeekAsTemplate(name: String, weekStartDate: LocalDate): Int

    suspend fun applyTemplate(templateId: String, weekStartDate: LocalDate)

    suspend fun deleteTemplate(templateId: String)
}
