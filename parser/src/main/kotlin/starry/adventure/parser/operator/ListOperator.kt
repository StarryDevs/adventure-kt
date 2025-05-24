package starry.adventure.parser.operator

import starry.adventure.parser.AbstractParser
import starry.adventure.parser.Parser
import starry.adventure.parser.symbol
import starry.adventure.parser.util.rule
import starry.adventure.parser.whitespace

class ListOperator<T : Any>(
    val element: Parser<T>,
    val prefix: String? = "(",
    val suffix: String? = ")",
    val separator: String? = ",",
    val allowSeparatorEnd: Boolean = false
) : AbstractParser<List<T>>() {

    val part by rule {
        +whitespace
        if (separator != null) +symbol(separator)
        +whitespace
        return@rule +element
    }

    override fun parse(): List<T> {
        if (prefix != null) +symbol(prefix)
        val elements = mutableListOf<T>()
        +whitespace
        val first = (+(element.optional())).getOrNull()
        +whitespace
        if (first != null) {
            elements += first
            while (true) {
                +whitespace
                val result = (+(part.optional())).getOrNull()
                +whitespace
                if (result == null) break
                else elements += result
            }
        }
        if (allowSeparatorEnd && separator != null) {
            +whitespace
            +symbol(separator).optional()
            +whitespace
        }
        if (suffix != null) {
            +symbol(suffix)
        }
        return elements
    }

}


fun <T : Any> Parser<T>.list(
    prefix: String? = "(",
    suffix: String? = ")",
    separator: String? = ",",
    allowSeparatorEnd: Boolean = false
) = ListOperator(this, prefix, suffix, separator, allowSeparatorEnd)
