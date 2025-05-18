package starry.adventure.brigadier.dispatcher

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future

open class CommandRequiredArgumentBuilder<S, T>(protected val requiredBuilder: RequiredArgumentBuilder<S, T>) :
    CommandArgumentBuilder<S, RequiredArgumentBuilder<S, T>>(requiredBuilder) {

    open fun suggests(block: suspend CommandContext<S>.(builder: SuggestionsBuilder) -> Unit) {
        requiredBuilder.suggests { ctx, builder ->
            CoroutineScope(Dispatchers.IO).future {
                block(ctx, builder)
                builder.build()
            }
        }
    }

}