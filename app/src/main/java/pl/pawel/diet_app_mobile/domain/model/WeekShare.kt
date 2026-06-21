package pl.pawel.diet_app_mobile.domain.model

/**
 * Przenośna (np. przez kod QR) reprezentacja planu jednego tygodnia. Sloty odwołują się do
 * posiłków po nazwie (a nie po lokalnym id), więc plan można odtworzyć na innym urządzeniu,
 * które ma te same posiłki (np. z seeda). Dni są względne do początku tygodnia (0–6).
 */
data class WeekShare(
    val label: String,
    val slots: List<WeekShareSlot>,
)

data class WeekShareSlot(
    val dayOffset: Int,
    val mealType: String,
    val mealName: String,
    val servings: Double,
)

/**
 * Wynik zastosowania udostępnionego tygodnia: ile slotów dodano i które posiłki pominięto
 * (bo nie ma ich w lokalnej bazie).
 */
data class ImportWeekResult(
    val applied: Int,
    val skipped: List<String>,
)
