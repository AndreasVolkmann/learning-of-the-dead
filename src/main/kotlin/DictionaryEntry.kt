data class DictionaryEntry(
    val timeGauge: Int,
    val phraseOffset: Int,
    val keycodeOffset: Int,
    val phrase: String,
    val keycodes: List<String>
)