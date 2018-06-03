package nuke.discord.command.meta.registry

import nuke.discord.bot.CommandBuilder
import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.Command
import nuke.discord.command.meta.selectors.CommandSelector
import nuke.discord.command.meta.selectors.ExactSelector

class CommandRegistry internal constructor(builder: RegistryBuilder) {

    val fallback = builder.fallback ?: FallbackCommand()
    val commands = builder.commands.apply { add(RegisteredCommand.Final("", fallback)) }

    constructor(selector: CommandSelector, builder: CommandBuilder)
            : this(RegistryBuilder(selector).apply(builder))

    fun search(name: String): RegisteredCommand? = commands.select(name)

    class RegistryBuilder internal constructor(
            internal val commands: CommandSelector) {
        internal var fallback: Command? = null

        // set fallback command
        fun fallback(command: Command?) {
            fallback = command
        }

        // register final command
        fun register(name: String, command: Command) {
            commands.add(RegisteredCommand.Final(name, command))
        }

        // register branch command with a default behaviour
        fun register(name: String, selector: CommandSelector, builder: (RegistryBuilder) -> Unit) {
            commands.add(CommandRegistry(selector, builder).let {
                RegisteredCommand.Branch(name, it.fallback, it)
            })
        }

        operator fun set(name: String, command: Command) = register(name, command)

        operator fun invoke(name: String, selector: CommandSelector = ExactSelector,
                            builder: (RegistryBuilder) -> Unit) = register(name, selector, builder)

    }

    inner class FallbackCommand : Command() {

        override fun onInvoke(context: CommandContext) {
            val list = commands.toMap()
                    .filter {
                        it.key.isNotEmpty()
                                && context.hasSufficientPermission(
                                it.value.command.requiredPermission)
                    }
                    .keys.joinToString(
                    prefix = "```\n",
                    separator = " | ",
                    postfix = "```"
            )

            context.replyFail("you haven't specified a valid sub-command.\n$list")
        }
    }

}