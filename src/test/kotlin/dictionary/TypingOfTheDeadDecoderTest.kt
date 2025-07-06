package dictionary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File

class TypingOfTheDeadDecoderTest {
    private val decoder = TypingOfTheDeadDecoder()
    private val entriesL010 = decoder.decodeDictionary("data/S000L010.bin")
    private val entriesL020 = decoder.decodeDictionary("data/S000L020.bin")

    data class ExpectedEntry(
        val timeGauge: Int,
        val phraseOffset: Int,
        val keycodeOffset: Int,
        val phrase: String,
        val keycodes: List<String>
    )
    
    @Test
    fun decodeShu() {
        val index = 790
        val entry = entriesL020[index]
        val expected = ExpectedEntry(
            timeGauge = 29,
            phraseOffset = 19324,
            keycodeOffset = 19332,
            phrase = "酒乱",
            keycodes = listOf("syu", "ra", "nn")
        )
        assertEntry(entry, expected, "Entry $index")
    }
    
    @Test
    fun decodeSha() {
        val index = 789
        val entry = entriesL020[index]
        val expected = ExpectedEntry(
            timeGauge = 29,
            phraseOffset = 0x4b6c,
            keycodeOffset = 0x4b78,
            phrase = "シシャモ",
            keycodes = listOf("si", "sya", "mo")
        )
        assertEntry(entry, expected, "Entry $index")
    }

    @Test
    fun decodeNumbers() {
        val index = 792
        val entry = entriesL020[index]
        val expected = ExpectedEntry(
            timeGauge = 29,
            phraseOffset = 19348,
            keycodeOffset = 19356,
            phrase = "パス２",
            keycodes = listOf("pa", "su", "2")
        )
        assertEntry(entry, expected, "Entry $index")
    }
    
    @TestFactory
    fun decode() = listOf(
        ExpectedEntry(0x9, 0xf54, 0xf5c, "ＤＨ", listOf("E", "I")),
        ExpectedEntry(0xa, 0xf60, 0xf68, "ＯＮ", listOf("P", "O")),
        ExpectedEntry(0xb, 0xf6c, 0xf74, "ＯＬ", listOf("P", "M")),
        ExpectedEntry(0xb, 0xf78, 0xf7c, "愛", listOf("a", "i")),
        ExpectedEntry(0xc, 0xf80, 0xf88, "ＩＱ", listOf("J", "R")),
        ExpectedEntry(0xc, 0xf8c, 0xf90, "王", listOf("o", "u")),
        ExpectedEntry(0xc, 0xf94, 0xf98, "茶", listOf("tya")),
        ExpectedEntry(0xd, 0xf9c, 0xfa0, "穴", listOf("a", "na")),
        ExpectedEntry(0xd, 0xfa4, 0xfac, "海女", listOf("a", "ma")),
        ExpectedEntry(0xd, 0xfb0, 0xfac, "アマ", listOf("a", "ma")),
        ExpectedEntry(0xd, 0xfb8, 0xfc0, "ＤＨＡ", listOf("E", "I", "B")),
        ExpectedEntry(0xe, 0xfc4, 0xfcc, "あざ", listOf("a", "za")),
        ExpectedEntry(0xe, 0xfd0, 0xfd4, "足", listOf("a", "si")),
        ExpectedEntry(0xe, 0xfd8, 0xfdc, "汗", listOf("a", "se")),
        ExpectedEntry(0xe, 0xfe0, 0xfe8, "アブ", listOf("a", "bu")),
    ).mapIndexed { i, entry -> DynamicTest.dynamicTest("$i") { 
        assertEntry(entriesL010[i], entry, "Entry $i")
    } }

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