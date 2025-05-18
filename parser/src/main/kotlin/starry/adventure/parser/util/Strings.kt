package starry.adventure.parser.util

import kotlinx.serialization.json.Json
import starry.adventure.parser.buffer
import starry.adventure.parser.symbol

val singleLineString by rule {
    var raw = +symbol("\"")
    while (buffer.get(buffer.position()) != '"') {
        if (buffer.get(buffer.position()) == '\\') {
            raw += buffer.get()
        }
        raw += buffer.get()
    }
    raw += buffer.get()
    try {
        Json.decodeFromString<String>(raw)
    } catch (_: Throwable) {
        throw makeError("Invalid string literal: $raw")
    }
}