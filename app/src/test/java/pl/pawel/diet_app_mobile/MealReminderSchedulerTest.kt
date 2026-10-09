package pl.pawel.diet_app_mobile

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import pl.pawel.diet_app_mobile.notifications.nextTriggerTime

class MealReminderSchedulerTest {

    private val warsaw = ZoneId.of("Europe/Warsaw")

    private fun at(text: String): ZonedDateTime = LocalDateTime.parse(text).atZone(warsaw)

    @Test
    fun laterTodayTriggersToday() {
        assertEquals(at("2026-10-09T14:00"), nextTriggerTime(LocalTime.of(14, 0), at("2026-10-09T09:30:15")))
    }

    @Test
    fun pastOrExactTimeTriggersTomorrow() {
        assertEquals(at("2026-10-10T08:00"), nextTriggerTime(LocalTime.of(8, 0), at("2026-10-09T09:30")))
        assertEquals(at("2026-10-10T08:00"), nextTriggerTime(LocalTime.of(8, 0), at("2026-10-09T08:00")))
    }

    @Test
    fun keepsWallClockTimeAcrossDstChange() {
        // 25.10.2026 w nocy zmiana czasu na zimowy (CEST -> CET).
        val next = nextTriggerTime(LocalTime.of(8, 0), at("2026-10-24T20:00"))
        assertEquals(LocalTime.of(8, 0), next.toLocalTime())
        assertEquals(at("2026-10-25T08:00"), next)
    }
}
