fun main() {
    val decoder = TypingOfTheDeadDecoder()
    val entries = decoder.decodeDictionary("data/S000L010.bin")
    entries.forEachIndexed(::print)
}

private fun print(index: Int, entry: DictionaryEntry) {
    println("Entry $index:")
    println("  Time Gauge: 0x${entry.timeGauge.toString(16)}")
    println("  Phrase Offset: 0x${entry.phraseOffset.toString(16)}")
    println("  Keycode Offset: 0x${entry.keycodeOffset.toString(16)}")
    println("  Phrase: ${entry.phrase}")
    println("  Keycodes: ${entry.keycodes.joinToString("")}")
    println()
}