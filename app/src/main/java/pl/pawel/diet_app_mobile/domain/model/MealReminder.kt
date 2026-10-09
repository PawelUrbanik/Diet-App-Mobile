package pl.pawel.diet_app_mobile.domain.model

import java.time.LocalTime

/**
 * Przypomnienie o posiłku danego typu (np. „Obiad”) wysyłane codziennie o [time].
 */
data class MealReminder(
    val mealType: String,
    val enabled: Boolean,
    val time: LocalTime,
)

object MealReminderDefaults {
    /** Domyślne godziny dla każdego typu posiłku; przypomnienia są domyślnie wyłączone. */
    val ALL: List<MealReminder> = listOf(
        MealReminder("Śniadanie", enabled = false, time = LocalTime.of(8, 0)),
        MealReminder("Drugie śniadanie", enabled = false, time = LocalTime.of(11, 0)),
        MealReminder("Obiad", enabled = false, time = LocalTime.of(14, 0)),
        MealReminder("Podwieczorek", enabled = false, time = LocalTime.of(17, 0)),
        MealReminder("Kolacja", enabled = false, time = LocalTime.of(20, 0)),
    )
}
