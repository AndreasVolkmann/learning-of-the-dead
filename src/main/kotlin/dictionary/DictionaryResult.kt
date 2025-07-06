package dictionary

data class DictionaryResult(
    val entries: List<DictionaryEntry>,
    val bytes: ByteArray,
    val firstValidTocOffset: Int,
    val lastValidTocOffset: Int
) {
}