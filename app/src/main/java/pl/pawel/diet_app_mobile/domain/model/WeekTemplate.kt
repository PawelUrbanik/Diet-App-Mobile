package pl.pawel.diet_app_mobile.domain.model

data class WeekTemplate(
    val id: String,
    val name: String,
    val isPredefined: Boolean,
    val slots: List<WeekTemplateSlot>,
) {
    val totalSlots: Int get() = slots.size
}

data class WeekTemplateSlot(
    val dayOffset: Int,
    val mealType: String,
    val meal: Meal,
    val servings: Double,
    val position: Int,
)
