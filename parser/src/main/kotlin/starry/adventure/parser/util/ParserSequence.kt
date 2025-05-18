package starry.adventure.parser.util

import starry.adventure.core.util.Delegate
import starry.adventure.parser.AbstractParser

class ParserSequence<T : Any>(name: String, private val block: AbstractParser<T>.() -> T) : AbstractParser<T>() {

    override val name: String = "ParserSequence::$name"
    override fun parse() = block()

}

fun <T : Any> rule(name: String, block: AbstractParser<T>.() -> T) = ParserSequence(name, block)
fun <T : Any> rule(block: AbstractParser<T>.() -> T): Delegate<Any?, ParserSequence<T>> {
    var name: String? = null
    val value by lazy {
        ParserSequence(name!!, block)
    }
    return Delegate<Any?, ParserSequence<T>> {
        name = it.name
        value
    }
}

