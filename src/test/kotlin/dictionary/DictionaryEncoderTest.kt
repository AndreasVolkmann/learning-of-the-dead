package dictionary

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DictionaryEncoderTest {
    private val encoder = DictionaryEncoder()
    private val decoder = DictionaryDecoder()
    
    private fun process(filePath: String) = decoder.process(DictionaryCommand(filePath)).entries
    
    @Test
    fun encodeAndDecode() {
        val entries = listOf(
            DictionaryEntry(
                timeGauge = 15,
                phraseOffset = 0,
                keycodeOffset = 0,
                phrase = "あい",
                keycodes = listOf("a", "i"),
                keycodeBytes = emptyList()
            ),
            DictionaryEntry(
                timeGauge = 20,
                phraseOffset = 0,
                keycodeOffset = 0,
                phrase = "酒乱",
                keycodes = listOf("syu", "ra", "nn"),
                keycodeBytes = emptyList()
            )
        )
        
        val encodedBytes = encoder.encode(entries)
        val decodedEntries = decoder.process(DictionaryCommand(encodedBytes)).entries
        
        assertEquals(entries.size, decodedEntries.size)
        for (i in entries.indices) {
            assertEquals(entries[i], decodedEntries[i])
        }
    }
}