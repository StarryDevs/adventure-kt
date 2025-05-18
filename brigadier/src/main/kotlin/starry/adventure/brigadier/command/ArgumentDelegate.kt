package starry.adventure.brigadier.command

import com.mojang.brigadier.context.CommandContext
import kotlin.reflect.KProperty

class ArgumentDelegate<S, T>(val context: CommandContext<S>, val name: String? = null, val type: Class<T>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = context.getArgument(name ?: property.name, type)

}

inline fun <S, reified T> CommandContext<S>.argument(name: String? = null) = ArgumentDelegate(this, name, T::class.java)
inline fun <S, reified T> CommandContext<S>.getArgument(name: String): T = getArgument(name, T::class.java)