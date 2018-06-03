package nuke.discord.bot

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.hooks.AnnotatedEventManager
import nuke.discord.command.meta.CommandService
import nuke.discord.command.meta.selectors.CommandSelector
import nuke.discord.util.Config

@Suppress("LeakingThis")
abstract class NukeBotBase(override val config: Config,
                           commandPrefix: String,
                           commandSelector: CommandSelector,
                           commandBuilder: CommandBuilder,
                           messageHandlers: List<MessageHandler>,
                           private val listeners: List<Any>) : NukeBot {

    override val commands = CommandService(
            this,
            commandPrefix, commandSelector, commandBuilder,
            messageHandlers
    )

    protected fun buildClient(preInit: JDABuilder.() -> Unit = {}): JDA = JDABuilder(AccountType.BOT).apply {
        setToken(config["token"])
        setEventManager(AnnotatedEventManager())

        addEventListener(commands)
        addEventListener(listeners)

        preInit()
    }.buildAsync()

}