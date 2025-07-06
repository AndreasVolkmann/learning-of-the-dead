import java.io.File
import java.nio.charset.Charset

data class DictionaryEntry(
    val timeGauge: Int,
    val phraseOffset: Int,
    val keycodeOffset: Int,
    val phrase: String,
    val keycodes: List<String>
)

class TypingOfTheDeadDecoder {
    private val shiftJIS = Charset.forName("Shift-JIS")
    
    // Keycode mapping from the provided JSON
    private val keycodeMap = mapOf(
        0xA1.toByte() to "0", 0xA2.toByte() to "1", 0xA3.toByte() to "2", 0xA4.toByte() to "3",
        0xA5.toByte() to "4", 0xA6.toByte() to "5", 0xA7.toByte() to "6", 0xA8.toByte() to "7",
        0xA9.toByte() to "8", 0xAA.toByte() to "9", 0xAB.toByte() to "A", 0xAC.toByte() to "B",
        0xAD.toByte() to "C", 0xAE.toByte() to "D", 0xAF.toByte() to "E", 0xB0.toByte() to "F",
        0xB1.toByte() to "G", 0xB2.toByte() to "H", 0xB3.toByte() to "I", 0xB4.toByte() to "J",
        0xB5.toByte() to "K", 0xB6.toByte() to "L", 0xB7.toByte() to "M", 0xB8.toByte() to "N",
        0xB9.toByte() to "O", 0xBA.toByte() to "P", 0xBB.toByte() to "Q", 0xBC.toByte() to "R",
        0xBD.toByte() to "S", 0xBE.toByte() to "T", 0xBF.toByte() to "U", 0xC0.toByte() to "V",
        0xC1.toByte() to "W", 0xC2.toByte() to "X", 0xC3.toByte() to "Y", 0xC4.toByte() to "Z",
        0xC5.toByte() to "a", 0xC6.toByte() to "b", 0xC7.toByte() to "c", 0xC8.toByte() to "d",
        0xC9.toByte() to "e", 0xCA.toByte() to "f", 0xCB.toByte() to "g", 0xCC.toByte() to "h",
        0xCD.toByte() to "i", 0xCE.toByte() to "j", 0xCF.toByte() to "k", 0xD0.toByte() to "l",
        0xD1.toByte() to "m", 0xD2.toByte() to "n", 0xD3.toByte() to "o", 0xD4.toByte() to "p",
        0xD5.toByte() to "q", 0xD6.toByte() to "r", 0xD7.toByte() to "s", 0xD8.toByte() to "t",
        0xD9.toByte() to "u", 0xDA.toByte() to "v", 0xDB.toByte() to "w", 0xDC.toByte() to "x",
        0xDD.toByte() to "y", 0xDE.toByte() to "z", 0xE3.toByte() to "!", 0xE4.toByte() to "_",
        0xE7.toByte() to "?", 0xE9.toByte() to "%", 0xEA.toByte() to " ", 0xEB.toByte() to "'",
        0xEC.toByte() to "$", 0xED.toByte() to "#", 0xEE.toByte() to "@", 0xEF.toByte() to ".",
        0xF0.toByte() to ",", 0xF1.toByte() to "-"
    )
    
    fun decodeDictionary(filePath: String): List<DictionaryEntry> {
        val file = File(filePath)
        val bytes = file.readBytes()
        val entries = mutableListOf<DictionaryEntry>()

        // Read TOC - each entry is 12 bytes (3 x 4-byte values)
        var tocOffset = 0
        while (tocOffset + 11 < bytes.size) {
            // Check for EOF marker (FF FF FF FF)
            if (bytes[tocOffset] == 0xFF.toByte() &&
                bytes[tocOffset + 1] == 0xFF.toByte() &&
                bytes[tocOffset + 2] == 0xFF.toByte() &&
                bytes[tocOffset + 3] == 0xFF.toByte()
            ) {
                break
            }

            // Read the three 4-byte values
            val timeGauge = readInt32(bytes, tocOffset)
            val phraseOffset = readInt32(bytes, tocOffset + 4)
            val keycodeOffset = readInt32(bytes, tocOffset + 8)

            // Read phrase and keycodes if offsets are valid
            val phrase = if (phraseOffset > 0 && phraseOffset < bytes.size) {
                readPhrase(bytes, phraseOffset)
            } else ""

            val keycodes = if (keycodeOffset > 0 && keycodeOffset < bytes.size) {
                readKeycodes(bytes, keycodeOffset)
            } else emptyList()

            entries.add(DictionaryEntry(timeGauge, phraseOffset, keycodeOffset, phrase, keycodes))
            tocOffset += 12
        }

        return entries
    }

    private fun readInt32(bytes: ByteArray, offset: Int): Int {
        // Read 4 bytes in little-endian order
        return (bytes[offset].toInt() and 0xFF) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 3].toInt() and 0xFF) shl 24)
    }

    private fun readPhrase(bytes: ByteArray, offset: Int): String {
        val phraseBytes = mutableListOf<Byte>()
        var i = offset

        // Read until we hit 0x00 or 0xFF
        while (i < bytes.size && bytes[i] != 0x00.toByte() && bytes[i] != 0xFF.toByte()) {
            phraseBytes.add(bytes[i])
            i++
        }

        return if (phraseBytes.isNotEmpty()) {
            String(phraseBytes.toByteArray(), shiftJIS)
        } else ""
    }

    private fun readKeycodes(bytes: ByteArray, offset: Int): List<String> {
        val keycodes = mutableListOf<String>()
        var i = offset

        // Read until we hit 0x00
        while (i < bytes.size && bytes[i] != 0x00.toByte()) {
            val keycode = keycodeMap[bytes[i]]
            if (keycode != null) {
                keycodes.add(keycode)
            } else {
                // Handle unknown keycodes
                keycodes.add("?")
            }
            i++
        }

        return keycodes
    }
}

fun main() {
    val decoder = TypingOfTheDeadDecoder()

    // Decode the dictionary file
    val entries = decoder.decodeDictionary("data/S000L010.bin")

    // Print results - only show entries with content
    entries.filter { it.phrase.isNotEmpty() || it.keycodes.isNotEmpty() }.forEachIndexed { index, entry ->
        println("Entry $index:")
        println("  Time Gauge: 0x${entry.timeGauge.toString(16)}")
        println("  Phrase Offset: 0x${entry.phraseOffset.toString(16)}")
        println("  Keycode Offset: 0x${entry.keycodeOffset.toString(16)}")
        println("  Phrase: ${entry.phrase}")
        println("  Keycodes: ${entry.keycodes.joinToString("")}")
        println()
    }
}