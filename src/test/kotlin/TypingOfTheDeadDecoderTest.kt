import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class TypingOfTheDeadDecoderTest {
    private val decoder = TypingOfTheDeadDecoder()

    data class ExpectedEntry(
        val timeGauge: Int,
        val phraseOffset: Int,
        val keycodeOffset: Int,
        val phrase: String,
        val keycodes: List<String>
    )

    @Test
    fun `test first few entries of S000L010 bin file`() {
        val entries = decoder.decodeDictionary("data/S000L010.bin")

        assertEquals(326, entries.size)

        val expectedEntries = listOf(
            ExpectedEntry(0x9, 0xf54, 0xf5c, "ＤＨ", listOf("E", "I")),
            ExpectedEntry(0xa, 0xf60, 0xf68, "ＯＮ", listOf("P", "O")),
            ExpectedEntry(0xb, 0xf6c, 0xf74, "ＯＬ", listOf("P", "M")),
            ExpectedEntry(0xb, 0xf78, 0xf7c, "愛", listOf("a", "i"))
        )

        expectedEntries.forEachIndexed { index, expected ->
            assertEntry(entries[index], expected, "Entry $index")
        }
    }

    @Test
    fun `test TOC parsing`() {
        val testFile = createTestFile()

        try {
            val entries = decoder.decodeDictionary(testFile.path)

            assertEquals(1, entries.size)
            assertEntry(
                entries[0],
                ExpectedEntry(0x09, 0xf54, 0xf5c, "", emptyList()),
                "TOC Entry"
            )
        } finally {
            testFile.delete()
        }
    }

    private fun assertEntry(actual: DictionaryEntry, expected: ExpectedEntry, entryName: String) {
        assertEquals(expected.timeGauge, actual.timeGauge, "$entryName timeGauge mismatch")
        assertEquals(expected.phraseOffset, actual.phraseOffset, "$entryName phraseOffset mismatch")
        assertEquals(expected.keycodeOffset, actual.keycodeOffset, "$entryName keycodeOffset mismatch")

        if (expected.phrase.isNotEmpty()) {
            assertEquals(expected.phrase, actual.phrase, "$entryName phrase mismatch")
        }

        if (expected.keycodes.isNotEmpty()) {
            assertEquals(expected.keycodes, actual.keycodes, "$entryName keycodes mismatch")
        }
    }

    private fun createTestFile(): File {
        val tocData = byteArrayOf(
            // Entry 1: timeGauge=0x09, phraseOffset=0xf54, keycodeOffset=0xf5c
            0x09, 0x00, 0x00, 0x00,
            0x54, 0x0f, 0x00, 0x00,
            0x5c, 0x0f, 0x00, 0x00,
            // EOF marker
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
        )

        return File("test_toc.bin").apply {
            writeBytes(tocData)
        }
    }
}