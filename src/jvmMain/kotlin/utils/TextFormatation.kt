package utils

data class TextSegment(
    val displayText: String,      // Original text for display (preserves formatting)
    val delimiter: String,
    val normalizedText: String = displayText  // Normalized text for TTS processing
)

fun splitSmartWithDelimiters(input: String): List<TextSegment> {
    val abbreviations = listOf("e.g.", "i.e.", "etc.", "vs.", "Mr.", "Dr.")
    val placeholderMap = mutableMapOf<String, String>()

    // Replace known abbreviations with placeholders
    var modified = input
    abbreviations.forEachIndexed { index, abbr ->
        val placeholder = "__ABBR_$index"
        modified = modified.replace(abbr, placeholder)
        placeholderMap[placeholder] = abbr
    }

    // Replace dotted identifiers (e.g., org.example.Class) with temporary tokens
    val dottedIdentifierRegex = Regex("""\b(?:[a-zA-Z_][\w]*\.)+[a-zA-Z_][\w]*\b""")
    val dottedMap = mutableMapOf<String, String>()
    val remainingDotIds = ArrayList<Int>()
    var idCount = 0
    modified = dottedIdentifierRegex.replace(modified) { matchResult ->
        val placeholder = "__DOT_ID_$idCount"
        dottedMap[placeholder] = matchResult.value
        remainingDotIds.add(idCount)
        idCount++
        placeholder
    }

    // Regex to match:
    // - a dot that's NOT part of a number or identifier: (?<!\d)\.(?!\d|\w)
    // - multiple newlines (paragraph breaks)
    val regex = Regex("""(?<!\d)\.(?![\d\w])|(\n\s*\n+)""")

    val result = mutableListOf<TextSegment>()
    var lastIndex = 0

    for (match in regex.findAll(modified)) {
        val end = match.range.first
        val segmentText = modified.substring(lastIndex, end)
        val delimiter = match.value

        result.add(TextSegment(segmentText, delimiter))

        lastIndex = match.range.last + 1
    }

    if (lastIndex < modified.length) {
        val remaining = modified.substring(lastIndex)
        if (remaining.isNotEmpty()) {
            result.add(TextSegment(remaining, ""))
        }
    }


    // Restore identifiers and abbreviations, and create normalized version
    return result.map { textSegment ->
        var restored = textSegment.displayText
        for (i in remainingDotIds.size - 1 downTo 0) {
            val key = "__DOT_ID_${remainingDotIds[i]}"
            if (restored.contains(key)) {
                remainingDotIds.remove(remainingDotIds[i])
            }
            restored = restored.replaceFirst(key, dottedMap[key].toString())
        }

        placeholderMap.forEach { (placeholder, abbr) ->
            restored = restored.replace(placeholder, abbr)
        }

        // Create normalized version: replace single newlines with spaces
        val normalized = restored.replace(Regex("""\n(?!\s*\n)"""), " ")

        TextSegment(
            displayText = restored,
            delimiter = textSegment.delimiter,
            normalizedText = normalized
        )
    }
}

fun reconstructFromSegments(segment: List<TextSegment>): String {
    return segment.joinToString("") { "${it.displayText}${it.delimiter}" }
}
