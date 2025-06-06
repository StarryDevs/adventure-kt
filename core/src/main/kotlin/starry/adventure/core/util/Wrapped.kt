package starry.adventure.core.util

interface IWrapped<T> {

    fun unwrap(): T

}

open class Wrapped<T>(private val value: T) : IWrapped<T> {

    override fun unwrap() = value

}

fun <T> T.wrapped() = Wrapped(this)
