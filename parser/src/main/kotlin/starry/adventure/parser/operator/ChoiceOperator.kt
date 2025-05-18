package starry.adventure.parser.operator

import starry.adventure.parser.AbstractParser
import starry.adventure.parser.Parser
import starry.adventure.parser.SyntaxError
import starry.adventure.parser.includeWithState

class ChoiceOperator<T : Any>(vararg val choices: Parser<out T>) : AbstractParser<T>() {

    override fun parse(): T {
        var exception: Throwable? = null
        for (choice in choices) {
            val (_, result) = includeWithState(choice.optional())
            if (result.isSuccess) return result.getOrNull() ?: throw makeError("Invalid syntax")
            else if (result.exceptionOrNull() is SyntaxError) {
                exception = result.exceptionOrNull()
                break
            }
        }
        if (exception != null) throw exception
        else throw makeError("Invalid syntax")
    }

}

fun <T : Any> choose(vararg choices: Parser<out T>) = ChoiceOperator(*choices)

@Suppress("UNCHECKED_CAST")
infix fun <T : Any> Parser<out T>.or(other: Parser<out T>): ChoiceOperator<T> {
    return if (this is ChoiceOperator<*>) choose(*this.choices, other) as ChoiceOperator<T>
    else if (other is ChoiceOperator<*>) choose(this, *other.choices) as ChoiceOperator<T>
    else choose(this, other)
}

infix fun <T : Any> ChoiceOperator<T>.not(filter: (Parser<out T>) -> Boolean) =
    ChoiceOperator(*this.choices.filter(filter).toTypedArray())

inline fun <reified T : Any> ChoiceOperator<T>.not() = not {
    it !is T && !T::class.isInstance(it)
}
