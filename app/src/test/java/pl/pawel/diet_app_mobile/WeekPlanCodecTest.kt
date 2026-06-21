package pl.pawel.diet_app_mobile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.pawel.diet_app_mobile.data.share.WeekPlanCodec
import pl.pawel.diet_app_mobile.domain.model.WeekShare
import pl.pawel.diet_app_mobile.domain.model.WeekShareSlot

class WeekPlanCodecTest {

    @Test
    fun roundTripPreservesAllSlots() {
        val share = WeekShare(
            label = "2020-06-15..2020-06-21",
            slots = listOf(
                WeekShareSlot(0, "Śniadanie", "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)", 1.0),
                WeekShareSlot(0, "Obiad", "MAKARON Z PESTO I KURCZAKIEM", 1.5),
                WeekShareSlot(3, "Drugie śniadanie", "TORTILLA Z ŁOSOSIEM, SERKIEM I SAŁATĄ LODOWĄ", 2.0),
                WeekShareSlot(6, "Kolacja", "OWSIANKA Z ODŻYWKĄ BIAŁKOWĄ (BANAN I ORZECHY)", 1.0),
            ),
        )

        val decoded = WeekPlanCodec.decode(WeekPlanCodec.encode(share))

        assertEquals(share, decoded)
    }

    @Test
    fun emptyPlanRoundTrips() {
        val share = WeekShare(label = "", slots = emptyList())
        assertEquals(share, WeekPlanCodec.decode(WeekPlanCodec.encode(share)))
    }

    @Test
    fun garbageInputReturnsNull() {
        assertNull(WeekPlanCodec.decode("to nie jest poprawny kod"))
    }
}
