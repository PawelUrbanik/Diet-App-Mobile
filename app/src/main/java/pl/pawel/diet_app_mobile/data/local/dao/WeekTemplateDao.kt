package pl.pawel.diet_app_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateSlotEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateWithSlots

@Dao
interface WeekTemplateDao {
    @Transaction
    @Query("SELECT * FROM week_templates ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WeekTemplateWithSlots>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTemplate(template: WeekTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSlots(slots: List<WeekTemplateSlotEntity>)

    @Query("DELETE FROM week_templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: Long)

    @Transaction
    suspend fun createTemplateWithSlots(
        template: WeekTemplateEntity,
        buildSlots: (templateId: Long) -> List<WeekTemplateSlotEntity>,
    ): Long {
        val templateId = insertTemplate(template)
        val slots = buildSlots(templateId)
        if (slots.isNotEmpty()) {
            insertSlots(slots)
        }
        return templateId
    }
}
