package starry.adventure.brigadier.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class JsonArgumentType<T>(val json: Json, val serializer: KSerializer<T>) : ArgumentType<T> {

    override fun parse(reader: StringReader) = json.decodeFromString(serializer, reader.readString())

}

inline fun <reified T> json(json: Json = Json) = JsonArgumentType(json, serializer<T>())
