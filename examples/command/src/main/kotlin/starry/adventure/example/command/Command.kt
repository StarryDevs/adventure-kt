package starry.adventure.example.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.tree.CommandNode
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import starry.adventure.brigadier.argument.json
import starry.adventure.brigadier.argument.parsing
import starry.adventure.brigadier.command.argument
import starry.adventure.brigadier.command.getArgument
import starry.adventure.brigadier.dispatcher.register
import starry.adventure.parser.impl.JsonParser
import java.io.PrintStream

open class Source(val appName: String, val printStream: PrintStream)

object Parser : DefaultParser() {

    override fun isEscapeChar(ch: Char) = false

}


fun main() {
    val source = Source(
        "adventure-examples-command",
        System.out
    )

    val dispatcher = CommandDispatcher<Source>()
    dispatcher.register {
        literal("json") {
            argument("json", parsing(JsonParser.json)) {
                run {
                    val json: Any by argument()
                    source.printStream.println(json)
                }
            }
        }
        literal("help") {
            argument("command", string()) {
                suggests { dispatcher.root.children.map(CommandNode<*>::getName).forEach(it::suggest) }
                run {
                    val command: String by argument()
                    source.printStream.println(
                        ansi().fgBrightGreen().a("$command ").a(
                            dispatcher.getSmartUsage(rootNode.getChild(command), source)
                                .map(Map.Entry<*, String>::value).joinToString(" ")
                        )
                    )
                }
            }
            run {
                dispatcher.getAllUsage(rootNode, source, false).forEach {
                    source.printStream.println(ansi().fgBrightGreen().a(it))
                }
            }
        }
        literal("map") {
            argument("json", json<Map<String, String>>()) {
                run {
                    val json: Map<String, String> by argument()
                    val keys = json.keys
                    val keyLength = keys.maxOf { it.length }
                    for ((key, value) in json) {
                        source.printStream.println("$key${" ".repeat(keyLength - key.length)} = $value")
                    }
                }
            }
        }
        literal("echo") {
            literal("text") {
                argument("content", greedyString()) {
                    run {
                        val content: String by argument()
                        source.printStream.println(content)
                    }
                }
            }
            literal("status") {
                argument("code", integer()) {
                    execute { getArgument("code") }
                    literal("text") {
                        argument("content", greedyString()) {
                            execute {
                                val code: Int by argument()
                                val content: String by argument()
                                source.printStream.println(content)
                                code
                            }
                        }
                    }
                }
            }
        }
    }

    val terminal: Terminal = TerminalBuilder.builder()
        .system(true)
        .jansi(true)
        // .dumb(false)
        .build()

    val completer = Completer { reader, line, candidates ->
        val cursor = line.cursor()
        val parsed = dispatcher.parse(line.line(), source)
        val suggestions = dispatcher.getCompletionSuggestions(parsed, cursor).get()
        suggestions
            .list
            .map(Suggestion::getText)
            .map(::Candidate)
            .forEach(candidates::add)
    }

    val reader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(completer)
        .parser(Parser)
        .build()

    while (true) {
        val line = reader.readLine(ansi().fgBrightBlue().a("> ").toString())
        try {
            val parsed = dispatcher.parse(line, source)
            val result = dispatcher.execute(parsed)
            println(ansi().a(Ansi.Attribute.ITALIC).fgBrightBlack().a("== $result"))
        } catch (throwable: Throwable) {
            println(ansi().fgRed().a(throwable.stackTraceToString()))
        }
    }
}
