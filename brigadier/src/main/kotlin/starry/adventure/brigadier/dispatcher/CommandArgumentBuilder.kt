package starry.adventure.brigadier.dispatcher

import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode

open class CommandArgumentBuilder<S, T : ArgumentBuilder<S, T>>(protected val builder: ArgumentBuilder<S, T>) {

    open fun literal(
        name: String,
        block: CommandArgumentBuilder<S, LiteralArgumentBuilder<S>>.() -> Unit
    ): CommandNode<S> {
        val context = CommandArgumentBuilder<S, LiteralArgumentBuilder<S>>(LiteralArgumentBuilder.literal(name))
        block(context)
        val built = context.build()
        builder.then(built)
        return built
    }

    open fun requires(block: S.() -> Boolean): CommandNode<S> = builder.requires(block).build()

    open fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>): CommandNode<S> =
        builder.fork(target, modifier).build()

    open fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>? = null): CommandNode<S> =
        if (modifier != null) builder.redirect(target, modifier).build()
        else builder.redirect(target).build()


    open fun <T> argument(
        name: String,
        type: ArgumentType<T>,
        block: CommandRequiredArgumentBuilder<S, T>.() -> Unit
    ): CommandNode<S?> {
        val context = CommandRequiredArgumentBuilder(argument<S, T>(name, type))
        block(context)
        val built = context.build()
        builder.then(built)
        return built
    }

    open fun execute(block: CommandContext<S>.() -> Int) {
        builder.executes(block)
    }

    open fun run(block: CommandContext<S>.() -> Unit) {
        builder.executes {
            block(it)
            0
        }
    }

    fun builder() = builder
    fun build(): CommandNode<S> = builder.build()

}

