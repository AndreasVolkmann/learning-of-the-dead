import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class TypingOfTheDeadDecoderTest {

    @Test
    fun `test first few entries of S000L010 bin file`() {
        val decoder = TypingOfTheDeadDecoder()
        val entries = decoder.decodeDictionary("data/S000L010.bin")

        // Filter entries with content
        val validEntries = entries.filter { it.phrase.isNotEmpty() || it.keycodes.isNotEmpty() }

        // Verify we have at least 4 entries
        assertTrue(validEntries.size >= 4, "Should have at least 4 valid entries")

        // Entry 0
        val entry0 = validEntries[0]
        assertEquals(0x9, entry0.timeGauge)
        assertEquals(0xf54, entry0.phraseOffset)
        assertEquals(0xf5c, entry0.keycodeOffset)
        assertEquals("ＤＨ", entry0.phrase)
        assertEquals(listOf("E", "I"), entry0.keycodes)

        // Entry 1
        val entry1 = validEntries[1]
        assertEquals(0xa, entry1.timeGauge)
        assertEquals(0xf60, entry1.phraseOffset)
        assertEquals(0xf68, entry1.keycodeOffset)
        assertEquals("ＯＮ", entry1.phrase)
        assertEquals(listOf("P", "O"), entry1.keycodes)

        // Entry 2
        val entry2 = validEntries[2]
        assertEquals(0xb, entry2.timeGauge)
        assertEquals(0xf6c, entry2.phraseOffset)
        assertEquals(0xf74, entry2.keycodeOffset)
        assertEquals("ＯＬ", entry2.phrase)
        assertEquals(listOf("P", "M"), entry2.keycodes)

        // Entry 3
        val entry3 = validEntries[3]
        assertEquals(0xb, entry3.timeGauge)
        assertEquals(0xf78, entry3.phraseOffset)
        assertEquals(0xf7c, entry3.keycodeOffset)
        assertEquals("愛", entry3.phrase)
        // Verify keycodes exist (exact values depend on the mapping)
        assertFalse(entry3.keycodes.isEmpty())
        assertNotEquals(listOf("?", "?"), entry3.keycodes)
    }

    @Test
    fun `test keycode mapping`() {
        val decoder = TypingOfTheDeadDecoder()

        // Test a few known keycode mappings
        val testBytes = byteArrayOf(
            0xAF.toByte(), // E
            0xB3.toByte(), // I
            0xBA.toByte(), // P
            0xB9.toByte()  // O
        )

        // Use reflection to access private method for testing
        val method = decoder.javaClass.getDeclaredMethod("readKeycodes", ByteArray::class.java, Int::class.java)
        method.isAccessible = true

        val keycodes = method.invoke(decoder, testBytes, 0) as List<String>
        assertEquals(listOf("E", "I", "P", "O"), keycodes)
    }

    @Test
    fun `test TOC parsing`() {
        // Create a minimal test file with known TOC entries
        val tocData = byteArrayOf(
            // Entry 1: timeGauge=0x09, phraseOffset=0xf54, keycodeOffset=0xf5c
            0x09, 0x00, 0x00, 0x00,
            0x54, 0x0f, 0x00, 0x00,
            0x5c, 0x0f, 0x00, 0x00,
            // EOF marker
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
        )

        val testFile = File("test_toc.bin")
        testFile.writeBytes(tocData)

        try {
            val decoder = TypingOfTheDeadDecoder()
            val entries = decoder.decodeDictionary("test_toc.bin")

            assertEquals(1, entries.size)
            assertEquals(0x09, entries[0].timeGauge)
            assertEquals(0xf54, entries[0].phraseOffset)
            assertEquals(0xf5c, entries[0].keycodeOffset)
        } finally {
            testFile.delete()
        }
    }
}