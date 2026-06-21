package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WeekTemplateWithSlots(
    @Embedded val template: WeekTemplateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId",
    )
    val slots: List<WeekTemplateSlotEntity>,
)
