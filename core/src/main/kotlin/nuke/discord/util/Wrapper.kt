package nuke.discord.util

data class Wrapper<T>(var o: T) {

    fun set(o: T) {
        this.o = o
    }

    fun unwrap(): T {
        return o
    }

    inline fun <R> with(crossinline block: (T) -> R) = o?.let(block)

}

fun Any?.wrap() = Wrapper(this)

