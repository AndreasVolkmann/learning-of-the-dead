package dictionary

class DictionaryDecoder : Encoder() {
    fun process(command: DictionaryCommand): DictionaryResult {
        val bytes = command.bytes
        val entries = mutableListOf<DictionaryEntry>()
        var firstValidTocOffset = 0
        var lastValidTocOffset = 0

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

            firstValidTocOffset = if (firstValidTocOffset == 0) tocOffset else firstValidTocOffset

            // Read the three 4-byte values
            val timeGauge = readInt32(bytes, tocOffset)
            val phraseOffset = readInt32(bytes, tocOffset + 4)
            val keycodeOffset = readInt32(bytes, tocOffset + 8)

            // Read phrase and keycodes if offsets are valid
            val phrase = if (phraseOffset > 0 && phraseOffset < bytes.size) {
                readPhrase(bytes, phraseOffset)
            } else ""

            val (keycodes, keycodeBytes) = if (keycodeOffset > 0 && keycodeOffset < bytes.size) {
                readKeycodes(bytes, keycodeOffset, phrase)
            } else Pair(emptyList(), emptyList())

            entries.add(DictionaryEntry(timeGauge, phraseOffset, keycodeOffset, phrase, keycodes, keycodeBytes))
            tocOffset += 12
        }
        
        lastValidTocOffset = tocOffset

        return DictionaryResult(
            entries, bytes, firstValidTocOffset, lastValidTocOffset)
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

    private fun readKeycodes(bytes: ByteArray, offset: Int, currentPhrase: String): Pair<List<String>, List<Byte>> {
        val keycodes = mutableListOf<String>()
        val keycodeBytes = mutableListOf<Byte>()
        var i = offset
        var keycodeIndex = 0

        // Read until we hit 0x00
        while (i < bytes.size && bytes[i] != 0x00.toByte()) {
            val byte = bytes[i]
            keycodeBytes.add(byte)
            val keycode = keycodeMap[byte]
            if (keycode != null) {
                keycodes.add(keycode)
            } else {
                // Debug unknown keycodes
                println("Unknown keycode at offset 0x${i.toString(16)}: 0x${"%02X".format(byte)} for phrase: '$currentPhrase' ($keycodeIndex)")
                keycodes.add("?")
            }
            i++
            keycodeIndex++
        }

        return Pair(keycodes, keycodeBytes)
    }

}