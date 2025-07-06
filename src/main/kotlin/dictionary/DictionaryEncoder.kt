package dictionary

import java.io.ByteArrayOutputStream

class DictionaryEncoder : Encoder() {
    fun encode(entries: List<Entry>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        
        // Calculate offsets for phrases and keycodes
        val tocSize = entries.size * 12 + 4 // 12 bytes per entry + 4 bytes for EOF marker
        var currentOffset = tocSize
        
        // Calculate actual offsets for each entry
        val calculatedEntries = entries.map { entry ->
            val phraseOffset = currentOffset
            val phraseBytes = entry.phrase.toByteArray(shiftJIS)
            currentOffset += phraseBytes.size + 1 // +1 for null terminator
            
            val keycodeOffset = currentOffset
            val keycodeBytes = encodeKeycodes(entry.keycodes)
            currentOffset += keycodeBytes.size + 1 // +1 for null terminator
            
            CalculatedEntry(
                timeGauge = entry.timeGauge,
                phraseOffset = phraseOffset,
                keycodeOffset = keycodeOffset,
                phraseBytes = phraseBytes,
                keycodeBytes = keycodeBytes
            )
        }
        
        // Write TOC
        calculatedEntries.forEach { entry ->
            outputStream.write(int32ToBytes(entry.timeGauge))
            outputStream.write(int32ToBytes(entry.phraseOffset))
            outputStream.write(int32ToBytes(entry.keycodeOffset))
        }
        
        // Write EOF marker
        outputStream.write(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()))
        
        // Write phrases and keycodes
        calculatedEntries.forEach { entry ->
            outputStream.write(entry.phraseBytes)
            outputStream.write(0x00) // null terminator
            outputStream.write(entry.keycodeBytes)
            outputStream.write(0x00) // null terminator
        }
        
        return outputStream.toByteArray()
    }
    
    private fun int32ToBytes(value: Int): ByteArray {
        // Convert to little-endian 4-byte array
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }
    
    private fun encodeKeycodes(keycodes: List<String>): ByteArray {
        val bytes = mutableListOf<Byte>()
        
        keycodes.forEach { keycode ->
            // Find the byte value for this keycode
            val byteValue = reverseMap[keycode]
            if (byteValue != null) {
                bytes.add(byteValue)
            } else {
                throw IllegalArgumentException("Unknown keycode: $keycode")
            }
        }
        
        return bytes.toByteArray()
    }
    
    private data class CalculatedEntry(
        val timeGauge: Int,
        val phraseOffset: Int,
        val keycodeOffset: Int,
        val phraseBytes: ByteArray,
        val keycodeBytes: ByteArray
    )
}