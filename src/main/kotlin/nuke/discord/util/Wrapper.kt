package nuke.discord.util

/**
 * Created by NuclearCoder on 2017-09-06.
 */

data class Wrapper<T>(var o: T)

fun <T> Wrapper<T>.set(o: T) {
    this.o = o
}

fun Any?.wrap() = Wrapper(this)
fun <T> Wrapper<T>.unwrap() = o

inline fun <T, R> Wrapper<T?>.with(crossinline block: (T) -> R) = o?.let(block)

