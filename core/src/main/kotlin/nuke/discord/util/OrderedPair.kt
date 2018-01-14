package nuke.discord.util

class OrderedPair<T : Comparable<T>>(first: T, second: T) {

    val first: T
    val second: T

    init {
        val lesser = first < second
        this.first = if (lesser) first else second
        this.second = if (lesser) second else first
    }

    operator fun component1() = first
    operator fun component2() = second

    override fun equals(other: Any?) = when (other) {
        is OrderedPair<*> -> first == other.first && second == other.second
        else -> super.equals(other)
    }

    override fun hashCode() = 31 * (31 + first.hashCode()) + second.hashCode()

}
