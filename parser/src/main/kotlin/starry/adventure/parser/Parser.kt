package starry.adventure.parser

import java.net.URI
import java.nio.CharBuffer

object AdventureParser {

    val DEFAULT_URI: URI = URI.create("about:blank")

}

open class ParserException(
    message: String?,
    val state: Parser.State?,
    val parser: Parser<*>,
    cause: Throwable? = null
) :
    Exception(message, cause) {

    override val message: String = "An error occurred while parsing ${parser.name}"

}

class SyntaxError(message: String?, state: Parser.State?, parser: Parser<*>, cause: Throwable? = null) :
    ParserException(message, state, parser, cause)

interface Parser<T : Any> {

    data class State(
        var buffer: CharBuffer,
        var uri: URI = AdventureParser.DEFAULT_URI,
        var parent: Parser<*>? = null
    )

    val name: String

    fun state(new: State?): Parser<T>
    fun state(): State?

    fun parse(): T
    fun makeError(message: String?, cause: Throwable? = null): ParserException
    fun makeSyntaxError(message: String?, cause: Throwable? = null): SyntaxError


    operator fun <R : Any> Parser<R>.unaryPlus(): R = this@Parser.include(this@unaryPlus)

}

abstract class AbstractParser<T : Any>(name: String? = null) : Parser<T> {

    override val name: String = (name ?: this::class.simpleName).toString()

    private var state: Parser.State? = null

    override fun state(new: Parser.State?): AbstractParser<T> {
        state = new
        return this
    }

    override fun state() = state
    override fun makeError(message: String?, cause: Throwable?): ParserException =
        ParserException(message, state(), this, cause)

    override fun makeSyntaxError(message: String?, cause: Throwable?): SyntaxError =
        SyntaxError(message, state(), this, cause)


}
