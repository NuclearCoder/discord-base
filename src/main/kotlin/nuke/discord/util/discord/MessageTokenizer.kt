package nuke.discord.util.discord

class MessageTokenizer(val text: String) {

    private var cursor: Int = 0

    /**
     * Moves the cursor to the given index.
     * The set cursor is guaranteed to stay in bounds.
     */
    fun seek(pos: Int) {
        cursor = pos.coerceIn(text.indices)
    }

    /**
     * Skips the given text and returns true if and only if there was a match.
     * This method advances the cursor only if the prefix is matched.
     */
    fun skip(prefix: String): Boolean {
        var length = 0
        while (length < prefix.length && cursor + length < text.length) {
            if (prefix[length] != text[length]) return false  // mismatch => don't skip
            length++
        }
        cursor += length  // match => move cursor
        return true
    }

    /**
     * Skips all whitespaces from the cursor.
     */
    fun skipWhitespaces() {
        while (cursor < text.length && text[cursor].isWhitespace()) {
            cursor++
        }
    }

    /**
     * Returns the next word that contains only matching characters, or null otherwise.
     * A word, here, is the text from the cursor (inclusive) to the next whitespace (exclusive).
     */
    fun nextMatchingOrNull(predicate: (Char) -> Boolean): String? {
        skipWhitespaces()
        val start = cursor
        while (cursor < text.length && !text[cursor].isWhitespace()) {
            if (!predicate(text[cursor])) {
                cursor = start // reset cursor
                return null
            }
            cursor++
        }
        return text.substring(start, cursor)
    }

    /**
     * Returns the next string that contains matching characters.
     */
    fun nextMatchingWhile(predicate: (Char) -> Boolean): String? {
        skipWhitespaces()
        val start = cursor
        while (cursor < text.length && !text[cursor].isWhitespace() && predicate(text[cursor])) {
            cursor++
        }
        return text.substring(start, cursor)
    }

    /**
     * Returns the next word, that is, the text from the cursor to the next whitespace (exclusive).
     * The returned string is empty if there was no non-empty word past the cursor.
     * The cursor skips all whitespaces preceding the word.
     */
    fun nextWord(): String {
        return nextMatchingOrNull { true } ?: "" // should never be null
    }

    /**
     * Returns the next n words, that is, n successions of words as defined in #nextWord.
     * The set of whitespaces separating the words are replaced with single whitespaces.
     */
    fun nextWords(n: Int): String {
        val builder = StringBuilder()
        builder.append(nextWord())
        (2..n).forEach {
            builder.append(' ')
            builder.append(nextWord())
        }
        return builder.toString()
    }

    /**
     * Returns the next integer separated by whitespaces.
     * The cursor skips all whitespaces preceding the number.
     * The cursor is only moved further if a number was found.
     */
    fun nextInt(): Int? {
        return try {
            nextMatchingWhile(Char::isDigit)?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Returns the next long integer separated by whitespaces.
     * The cursor skips all whitespaces preceding the number.
     * The cursor is only moved further if a number was found.
     */
    fun nextLong(): Long? {
        return try {
            nextMatchingWhile(Char::isDigit)?.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Returns the next user mention or null if there is no match.
     * The cursor skips all whitespaces preceding the mention.
     * The cursor is only moved further if a user mention was found.
     */
    fun nextUserMention(): Long? {
        skipWhitespaces()
        // a user mention is <@xxx> or <@!xxx>
        val initialCursor = cursor

        // WARNING: big mess to parse mentions without regular expressions

        val userID = if (cursor + 4 < text.length) {
            if (text[cursor++] == '<' && text[cursor++] == '@') {
                if (text[cursor] == '!') cursor++ // skip the ! nickname mention character
                val number = nextLong()
                if (cursor < text.length && text[cursor++] == '>') number
                else null
            } else null
        } else null

        if (userID == null) {
            cursor = initialCursor // reset cursor if no mention was found
        }

        return userID
    }

    /**
     * Returns the next role mention or null if there is no match.
     * The cursor skips all whitespaces preceding the mention.
     * The cursor is only moved further if a role mention was found.
     */
    fun nextRoleMention(): Long? {
        skipWhitespaces()
        // a role mention is <&xxx>
        val initialCursor = cursor

        // WARNING: big mess to parse mentions without regular expressions

        val roleID = if (cursor + 3 < text.length) {
            if (text[cursor++] == '<' && text[cursor++] == '&') {
                val number = nextLong()
                if (cursor < text.length && text[cursor++] == '>') number
                else null
            } else null
        } else null

        if (roleID == null) {
            cursor = initialCursor // reset cursor if no mention was found
        }

        return roleID
    }

    /**
     * Returns the next channel mention or null if there is no match.
     * The cursor skips all whitespaces preceding the mention.
     * The cursor is only moved further if a channel mention was found.
     */
    fun nextChannelMention(): Long? {
        skipWhitespaces()
        // a channel mention is <#xxx>
        val initialCursor = cursor

        // WARNING: big mess to parse mentions without regular expressions

        val channelId = if (cursor + 3 < text.length) {
            if (text[cursor++] == '<' && text[cursor++] == '#') {
                val number = nextLong()
                if (cursor < text.length && text[cursor++] == '>') number
                else null
            } else null
        } else null

        if (channelId == null) {
            cursor = initialCursor // reset cursor if no mention was found
        }

        return channelId
    }

    /**
     * Returns the rest of the string as-is, starting for the first non-whitespace character.
     */
    fun tail(): String {
        skipWhitespaces()
        return if (cursor >= text.length) "" else text.substring(cursor, text.length)
    }

    /**
     * Returns the rest of the string as-is, starting from the first non-whitespace character,
     * until the first character satisfying the predicate (exclusive).
     */
    fun tailUntil(predicate: (Char) -> Boolean): String {
        skipWhitespaces()
        val start = cursor
        while (cursor < text.length && !predicate(text[cursor])) {
            cursor++
        }
        return text.substring(start, cursor)
    }

    /**
     * True if and only if the cursor hasn't reached the end.
     */
    val hasMore: Boolean
        get() = cursor < text.length - 1

}