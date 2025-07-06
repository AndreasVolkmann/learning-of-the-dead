import dictionary.DictionaryCommand
import dictionary.DictionaryEntry
import dictionary.DictionaryDecoder
import dictionary.DictionaryEncoder
import dictionary.Entry
import java.io.File

fun main() {
    val file = "S000L010.bin"
    val newEntries = listOf(
        Entry("標準", listOf("B", "i", "a", "o", "1", "Z", "H", "u", "N", "3")),
        Entry(
            timeGauge = 15,
            phrase = "あい",
            keycodes = listOf("a", "i"),
        ),
        Entry(
            timeGauge = 20,
            phrase = "酒乱",
            keycodes = listOf("shu", "ra", "nn")
        ),
    )

    generate(file, newEntries)
    extract("output/$file")
}

private fun extract(filePath: String) {
    val decoder = DictionaryDecoder()
    val command = DictionaryCommand(filePath)
    val result = decoder.process(command)
    result.entries.forEachIndexed(::print)

    println(result.firstValidTocOffset)
    println(result.lastValidTocOffset)
}

private fun generate(file: String, newEntries: List<Entry>) {
    val first12Bytes = File("data/${file}").readBytes().take(12)
    val newBytes = DictionaryEncoder().encode(newEntries)
//    val bytes = ByteArray(12 + newBytes.size)
//    first12Bytes.forEachIndexed { index, byte ->
//        bytes[index] = byte
//    }
//    newBytes.forEachIndexed { index, byte ->
//        bytes[12 + index] = byte
//    }
//    File("output/${file}").writeBytes(bytes)

    File("output/${file}").writeBytes(newBytes)
    
}

private fun print(index: Int, entry: DictionaryEntry) {
    println("Entry $index:")
    println("  Time Gauge: 0x${entry.timeGauge}")
    println("  Phrase Offset: 0x${entry.phraseOffset}")
    println("  Keycode Offset: 0x${entry.keycodeOffset}")
    println("  Phrase: ${entry.phrase}")
    println("  Keycodes: ${entry.keycodes.joinToString("")}")
    println("  Keycode Bytes: ${entry.keycodeBytes.joinToString(" ") { "0x%02X".format(it) }}")
    println()
}

