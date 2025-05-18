package starry.adventure.core.util

class Wrapped<T>(private val value: T) {

    fun unwrap() = value

}

fun <T> T.wrapped() = Wrapped(this)
