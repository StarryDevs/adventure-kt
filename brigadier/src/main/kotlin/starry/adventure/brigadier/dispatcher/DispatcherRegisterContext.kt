package starry.adventure.brigadier.dispatcher

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.tree.CommandNode

open class DispatcherRegisterContext<S>(val dispatcher: CommandDispatcher<S>) {

    fun literal(name: String, block: CommandArgumentBuilder<S, LiteralArgumentBuilder<S>>.() -> Unit): CommandNode<S> {
        val context = CommandArgumentBuilder(literal<S>(name))
        block(context)
        dispatcher.register(context.builder() as LiteralArgumentBuilder)
        return context.build()
    }

}

fun <S> CommandDispatcher<S>.register(block: DispatcherRegisterContext<S>.() -> Unit) {
    val context = DispatcherRegisterContext(this)
    block(context)
}
