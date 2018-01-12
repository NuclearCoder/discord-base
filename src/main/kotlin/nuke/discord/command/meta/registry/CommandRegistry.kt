package nuke.discord.command.meta.registry

import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.Command
import org.apache.commons.collections4.trie.PatriciaTrie

class CommandRegistry private constructor(builder: RegistryBuilder) {

    val fallback = builder.fallback ?: FallbackCommand()
    val commands = builder.commands.also { it[""] = RegisteredCommand.Final("", fallback) }

    constructor(builder: (RegistryBuilder) -> Unit)
            : this(RegistryBuilder().apply(builder))

    fun search(name: String): RegisteredCommand? = commands.selectValue(name)

    class RegistryBuilder internal constructor() {

        internal var fallback: Command? = null
        internal val commands = PatriciaTrie<RegisteredCommand>()

        // set fallback command
        fun fallback(command: Command?) {
            fallback = command
        }

        // register final command
        fun register(name: String, command: Command) {
            commands[name] = RegisteredCommand.Final(name, command)
        }

        // register branch command with a default behaviour
        fun register(name: String, builder: (RegistryBuilder) -> Unit) {
            commands[name] = CommandRegistry(builder).let {
                RegisteredCommand.Branch(name, it.fallback, it)
            }
        }

        operator fun set(name: String, command: Command) = register(name, command)
        operator fun invoke(name: String, builder: (RegistryBuilder) -> Unit) = register(name, builder)

    }

    inner class FallbackCommand : Command() {

        override fun onInvoke(context: CommandContext) {
            val list = commands.entries.filter { it.key.isNotEmpty() && context.hasSufficientPermission(it.value.command.requiredPermission) }.joinToString(prefix = "```\n", separator = " | ", postfix = "```") { it.key }

            context.replyFail("you haven't specified a valid sub-command.\n$list")
        }

    }

}