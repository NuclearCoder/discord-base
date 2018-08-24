package nuke.discord.command.meta.registry

import com.thatsnomoon.kda.buildEmbed
import nuke.discord.bot.CommandBuilder
import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.Command
import nuke.discord.command.meta.selectors.CommandSelector
import nuke.discord.command.meta.selectors.ExactSelector

class CommandRegistry internal constructor(builder: RegistryBuilder) {

    val fallback = builder.fallback ?: FallbackCommand()
    val commands = builder.commands.apply {
        add(RegisteredCommand.Final("", fallback))
        if (builder.registerHelp) {
            add(RegisteredCommand.Final("help", HelpCommand()))
        }
    }

    constructor(selector: CommandSelector, builder: CommandBuilder)
            : this(RegistryBuilder(selector).apply(builder))

    fun search(name: String): RegisteredCommand? = commands.select(name)

    class RegistryBuilder internal constructor(
            internal val commands: CommandSelector) {
        internal var fallback: Command? = null
        var registerHelp = true

        // set fallback command
        fun fallback(command: Command?) {
            fallback = command
        }

        // register final command
        fun register(name: String, command: Command) {
            commands.add(RegisteredCommand.Final(name, command))
        }

        // register branch command with a default behaviour
        fun register(name: String, selector: CommandSelector,
                     description: String = Command.defaultDescription, builder: (RegistryBuilder) -> Unit) {
            commands.add(CommandRegistry(selector, builder).let {
                RegisteredCommand.Branch(name, it.fallback, description, it)
            })
        }

        operator fun set(name: String, command: Command) = register(name, command)

        operator fun invoke(name: String, selector: CommandSelector = ExactSelector,
                            description: String = Command.defaultDescription,
                            builder: (RegistryBuilder) -> Unit) = register(name, selector, description, builder)

    }

    inner class FallbackCommand : Command() {
        override fun onInvoke(context: CommandContext) {
            val list = commands.toMap()
                    .filter { (name, reg) ->
                        name.isNotEmpty()
                            && context.hasSufficientPermission(
                                reg.command.requiredPermission)
                    }
                    .keys.joinToString(
                    prefix = "```\n",
                    separator = " | ",
                    postfix = "```"
            )

            context.replyFail("you haven't specified a valid sub-command.\n$list")
        }
    }

    inner class HelpCommand : Command(description = "Prints this help.") {
        override fun onInvoke(context: CommandContext) {
            buildEmbed {
                setAuthor("Help", null, context.jda.selfUser.effectiveAvatarUrl)

                commands.toMap().filterKeys(String::isNotEmpty).forEach { (name, reg) ->
                    when (reg) {
                        is RegisteredCommand.Final -> addField(name, reg.command.description, false)
                        is RegisteredCommand.Branch -> addField(name, reg.description, false)
                    }
                }
            }
        }
    }

}