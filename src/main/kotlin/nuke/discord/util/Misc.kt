package nuke.discord.util

import org.w3c.dom.NodeList

fun stringFromTime(time: Int): String {
    val sec = time % 60
    val minTime = time / 60
    val min = minTime % 60
    val hrs = minTime / 60

    val sb = StringBuilder()

    if (hrs > 0) {
        if (hrs < 10)
            sb.append('0')
        sb.append(hrs)
        sb.append(':')
    }

    if (min < 10)
        sb.append('0')
    sb.append(min)
    sb.append(':')

    if (sec < 10)
        sb.append('0')
    sb.append(sec)

    return sb.toString()
}

fun parseTime(string: String): Int {
    // that is probably an awful way to parse time,
    // however it should be heaps more efficient than regular expressions

    val parts = string.split(':')

    if (parts.size !in 2..3)
        throw NumberFormatException("Not a time format")

    var i = 0

    val hrs = if (parts.size < 3) 0 else Integer.parseUnsignedInt(parts[i++])
    val min = Integer.parseUnsignedInt(parts[i++])
    val sec = Integer.parseUnsignedInt(parts[i])

    if (min >= 60 || sec >= 60)
        throw NumberFormatException("Not a time format")

    return (hrs * 60 + min) * 60 + sec
}

inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Exception) {
        }
        throw e
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}

inline fun <R> ignore(crossinline block: () -> R) =
    try {
        block()
    } catch (ignored: Exception) {
        null
    }

fun NodeList.toIterable() = (0 until length).map(this::item)

fun <T> T?.notNull(message: String = "Receiver is null"): T = this ?: error(message)

fun Int.ifBetween(lo: Int, hi: Int, block: (Int) -> Unit) = this.takeIf { it in lo until hi }?.let(block)