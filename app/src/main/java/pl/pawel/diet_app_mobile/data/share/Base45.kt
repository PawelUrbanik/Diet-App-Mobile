package pl.pawel.diet_app_mobile.data.share

import java.io.ByteArrayOutputStream

/**
 * Kodowanie Base45 (RFC 9285) — gęstsze w trybie alfanumerycznym kodu QR niż Base64.
 */
internal object Base45 {
    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ \$%*+-./:"
    private val REVERSE = IntArray(128) { -1 }.also { table ->
        ALPHABET.forEachIndexed { index, c -> table[c.code] = index }
    }

    fun encode(data: ByteArray): String {
        val sb = StringBuilder()
        var i = 0
        while (i + 1 < data.size) {
            val value = ((data[i].toInt() and 0xFF) shl 8) or (data[i + 1].toInt() and 0xFF)
            sb.append(ALPHABET[value % 45])
            sb.append(ALPHABET[(value / 45) % 45])
            sb.append(ALPHABET[(value / 45 / 45) % 45])
            i += 2
        }
        if (i < data.size) {
            val value = data[i].toInt() and 0xFF
            sb.append(ALPHABET[value % 45])
            sb.append(ALPHABET[(value / 45) % 45])
        }
        return sb.toString()
    }

    fun decode(text: String): ByteArray {
        val out = ByteArrayOutputStream()
        var i = 0
        while (i + 2 < text.length) {
            val value = symbol(text[i]) + symbol(text[i + 1]) * 45 + symbol(text[i + 2]) * 45 * 45
            require(value in 0..0xFFFF) { "Nieprawidłowy Base45" }
            out.write((value ushr 8) and 0xFF)
            out.write(value and 0xFF)
            i += 3
        }
        val remaining = text.length - i
        if (remaining == 2) {
            val value = symbol(text[i]) + symbol(text[i + 1]) * 45
            require(value in 0..0xFF) { "Nieprawidłowy Base45" }
            out.write(value and 0xFF)
        } else if (remaining != 0) {
            error("Nieprawidłowa długość Base45")
        }
        return out.toByteArray()
    }

    private fun symbol(c: Char): Int {
        val value = if (c.code < 128) REVERSE[c.code] else -1
        require(value >= 0) { "Nieprawidłowy znak Base45: $c" }
        return value
    }
}
