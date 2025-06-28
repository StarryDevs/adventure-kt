package starry.adventure.parser.operator

import starry.adventure.parser.AbstractParser
import starry.adventure.parser.Parser
import starry.adventure.parser.buffer
import starry.adventure.parser.include
import starry.adventure.parser.util.rule

class OptionalOperator<T : Any>(val parser: Parser<T>, val rollback: Boolean = true) : AbstractParser<Result<T>>() {

    override fun parse(): Result<T> {
        val position = buffer.position()
        try {
            val result = Result.success(+parser)
            return result
        } catch (throwable: Throwable) {
            if (rollback) {
                buffer.position(position)
            }
            return Result.failure(throwable)
        }
    }

}

fun <R : Any> Parser<R>.optional(rollback: Boolean = true) = OptionalOperator(this, rollback)

fun <T : Any> Parser<Result<T>>.orElse(block: (Throwable) -> T) = rule("orElse") {
    include(this@orElse).getOrElse(block)
}

fun <T : Any> Parser<Result<T>>.orElse(parser: Parser<T>) = rule("orElse") {
    include(this@orElse).getOrElse {
        include(parser)
    }
}

