package starry.adventure.parser.impl

import starry.adventure.parser.buffer
import starry.adventure.parser.map
import starry.adventure.parser.operator.choose
import starry.adventure.parser.operator.list
import starry.adventure.parser.symbol
import starry.adventure.parser.util.ParserSequence
import starry.adventure.parser.util.rule
import starry.adventure.parser.util.singleLineString

object JsonParser {

    val jsonString = singleLineString

    val jsonNumber by rule {
        var raw = ""
        var character: Char
        while (true) {
            try {
                character = buffer.get()
            } catch (_: Throwable) {
                break
            }
            if (!(character.isDigit() || (character == '.' && '.' !in raw) || (character.lowercase() == "e" && 'e' !in raw.lowercase()) || (character in "+-" && (raw.last()
                    .lowercase() == "e")))
            ) {
                buffer.position(buffer.position() - 1)
                break
            }
            raw += character
        }
        if (raw.isEmpty()) throw makeError("Invalid or unexpected token")
        raw.toDouble()
    }

    val jsonNull = symbol("null").map {}
    val jsonTrue = symbol("true").map { true }
    val jsonFalse = symbol("false").map { false }
    val json = json()
    val jsonEntry = jsonEntry()
    val jsonLiteral = jsonLiteral()
    val jsonArray = jsonArray()
    val jsonObject = jsonObject()

    private fun json(): ParserSequence<Any> = rule("Json") {
        +choose(jsonObject, jsonArray, jsonLiteral)
    }

    private fun jsonEntry(): ParserSequence<Pair<String, Any>> = rule("jsonEntry") {
        val string = +jsonString
        +symbol(":")
        val value = +json
        string to value
    }

    private fun jsonLiteral(): ParserSequence<Any> = rule("jsonValue") {
        +choose(jsonNumber, jsonNull, jsonString, jsonTrue, jsonFalse)
    }

    private fun jsonArray(): ParserSequence<List<Any>> = rule("jsonArray") {
        val array = mutableListOf<Any>()
        val values = +json.list("[", "]")
        array.addAll(values)
        array
    }

    private fun jsonObject(): ParserSequence<Map<String, Any>> = rule("JsonObject") {
        val obj = mutableMapOf<String, Any>()
        val entries = +jsonEntry.list("{", "}")
        obj.putAll(entries)
        obj
    }


}