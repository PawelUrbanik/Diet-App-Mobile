package pl.pawel.diet_app_mobile.data.share

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater
import pl.pawel.diet_app_mobile.domain.model.WeekShare
import pl.pawel.diet_app_mobile.domain.model.WeekShareSlot

/**
 * Serializacja planu tygodnia do/z kompaktowego tekstu nadającego się do kodu QR.
 * Format „v1": nagłówek, etykieta, słownik nazw posiłków i typów (deduplikacja), lista slotów
 * jako liczby — całość spakowana raw-deflate i zakodowana Base45.
 */
object WeekPlanCodec {
    private const val MAGIC = "DAP1"

    fun encode(share: WeekShare): String {
        val names = share.slots.map { it.mealName }.distinct()
        val types = share.slots.map { it.mealType }.distinct()
        val nameIndex = names.withIndex().associate { (index, value) -> value to index }
        val typeIndex = types.withIndex().associate { (index, value) -> value to index }

        val sb = StringBuilder()
        sb.append(MAGIC).append('\n')
        sb.append(share.label).append('\n')
        sb.append('M').append(names.size).append('\n')
        names.forEach { sb.append(it).append('\n') }
        sb.append('T').append(types.size).append('\n')
        types.forEach { sb.append(it).append('\n') }
        sb.append('S').append(share.slots.size).append('\n')
        share.slots.forEach { slot ->
            sb.append(slot.dayOffset).append(',')
                .append(typeIndex.getValue(slot.mealType)).append(',')
                .append(nameIndex.getValue(slot.mealName)).append(',')
                .append(servingsToCode(slot.servings)).append('\n')
        }

        return Base45.encode(deflate(sb.toString().toByteArray(Charsets.UTF_8)))
    }

    fun decode(encoded: String): WeekShare? = runCatching {
        val text = String(inflate(Base45.decode(encoded.trim())), Charsets.UTF_8)
        val lines = text.split('\n')
        var cursor = 0
        fun next(): String = lines[cursor++]

        if (next() != MAGIC) return null
        val label = next()

        val names = readSection(::next, 'M')
        val types = readSection(::next, 'T')

        val slotHeader = next()
        require(slotHeader.startsWith("S"))
        val slotCount = slotHeader.substring(1).toInt()
        val slots = ArrayList<WeekShareSlot>(slotCount)
        repeat(slotCount) {
            val parts = next().split(',')
            slots.add(
                WeekShareSlot(
                    dayOffset = parts[0].toInt(),
                    mealType = types[parts[1].toInt()],
                    mealName = names[parts[2].toInt()],
                    servings = codeToServings(parts[3].toInt()),
                ),
            )
        }
        WeekShare(label = label, slots = slots)
    }.getOrNull()

    private inline fun readSection(next: () -> String, marker: Char): List<String> {
        val header = next()
        require(header.startsWith(marker))
        val count = header.substring(1).toInt()
        return List(count) { next() }
    }

    private fun servingsToCode(servings: Double): Int = (servings * 2).toInt()

    private fun codeToServings(code: Int): Double = code / 2.0

    private fun deflate(input: ByteArray): ByteArray {
        val deflater = Deflater(Deflater.BEST_COMPRESSION, true)
        deflater.setInput(input)
        deflater.finish()
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            out.write(buffer, 0, deflater.deflate(buffer))
        }
        deflater.end()
        return out.toByteArray()
    }

    private fun inflate(input: ByteArray): ByteArray {
        val inflater = Inflater(true)
        inflater.setInput(input)
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val n = inflater.inflate(buffer)
            if (n == 0 && inflater.needsInput()) break
            out.write(buffer, 0, n)
        }
        inflater.end()
        return out.toByteArray()
    }
}
