package pl.pawel.diet_app_mobile.domain.model

/**
 * Skrótowy opis posiłku zaplanowanego w danym zakresie dat — używany przy generowaniu
 * listy zakupów, by można było wykluczyć posiłek (wszystkie jego wystąpienia).
 */
data class PlannedMealSummary(
    val mealId: Long,
    val name: String,
    val occurrences: Int,
)
