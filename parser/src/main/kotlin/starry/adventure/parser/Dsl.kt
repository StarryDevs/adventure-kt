package starry.adventure.parser

import starry.adventure.parser.util.rule
import java.nio.CharBuffer

var Parser<*>.state: Parser.State
    get() = state()!!
    set(value) = Unit.also { state(value) }

var Parser<*>.buffer: CharBuffer
    get() = state.buffer
    set(value) = Unit.also { state(state()!!.copy(buffer = value)) }

fun <R : Any> Parser<*>.include(parser: Parser<R>): R = includeWithState(parser).second

fun <R : Any> Parser<*>.includeWithState(parser: Parser<R>): Pair<Parser.State, R> {
    val origin = parser.state()
    parser.state(this.state()?.copy(parent = this))
    val result = parser.parse()
    val resultState = parser.state()
    parser.state(origin)
    return resultState!! to result
}

fun CharBuffer.peek(offset: Int = 1) = get(position() + offset - 1)
fun CharBuffer.skip(offset: Int = 1): CharBuffer = position(position() + offset)

fun <T : Any> CharBuffer.parse(parser: Parser<T>, state: Parser.State = Parser.State(this)): T {
    parser.state(state.copy(buffer = this))
    val result = parser.parse()
    parser.state(null)
    return result
}

val whitespace by rule {
    var result = ""
    while (buffer.peek().isWhitespace())
        result += buffer.get()
    result
}

fun symbol(name: String) = rule("symbol($name)") {
    for (char in name) {
        val current = buffer.get()
        if (current != char) throw makeError("Expected '$char', but got '$current'")
    }
    name
}

fun character(predicate: (Char) -> Boolean) = rule("character") {
    val current = buffer.get()
    if (!predicate(current)) throw makeError("Invalid character '$current'")
    current
}

fun <T : Any, R : Any> Parser<T>.map(block: (T) -> R) = rule("$name::map") {
    block(+this@map)
}

fun <T, R> Parser<out Iterable<T>>.mapEach(block: (T) -> R) = rule("$name::mapEach") {
    (+this@mapEach).map(block)
}

fun <T, R> Parser<out Iterable<T>>.mapEachIndexed(block: (Int, T) -> R) = rule("$name::mapEachIndexed") {
    (+this@mapEachIndexed).mapIndexed(block)
}
