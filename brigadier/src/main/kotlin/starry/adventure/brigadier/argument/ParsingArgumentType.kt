package starry.adventure.brigadier.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import starry.adventure.parser.Parser
import starry.adventure.parser.parse
import java.nio.CharBuffer

class ParsingArgumentType<T : Any>(val parser: Parser<T>, val state: Parser.State? = null) : ArgumentType<T> {

    companion object {
        val SYNTAX_EXCEPTION = SimpleCommandExceptionType { "Invalid syntax" }
    }

    override fun parse(reader: StringReader): T {
        val charBuffer = CharBuffer.wrap(reader.string)
        charBuffer.position(reader.cursor)
        val result = if (state == null) charBuffer.parse(parser) else charBuffer.parse(parser, state)
        reader.cursor = charBuffer.position()
        return result
    }

}

fun <T : Any> parsing(parser: Parser<T>, state: Parser.State? = null) = ParsingArgumentType(parser, state)
