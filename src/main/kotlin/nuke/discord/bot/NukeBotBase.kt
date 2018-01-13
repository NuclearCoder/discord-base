package nuke.discord.bot

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.InterfacedEventManager
import nuke.discord.command.meta.CommandService
import nuke.discord.util.Config

@Suppress("LeakingThis")
abstract class NukeBotBase(override val config: Config,
                           commandPrefix: String,
                           commandBuilder: CommandBuilder,
                           messageHandlers: List<MessageHandler>,
                           private val listeners: List<EventListener>) : NukeBot {

    override val commands = CommandService(
            this, commandPrefix, commandBuilder, messageHandlers
    )

    protected fun buildClient(preInit: JDABuilder.() -> Unit = {}): JDA = JDABuilder(AccountType.BOT).apply {
        setToken(config["token"])
        //setEventManager(AnnotatedEventManager())
        setEventManager(InterfacedEventManager())

        addEventListener(commands)
        listeners.forEach { addEventListener(it) }

        preInit()
    }.buildAsync()

}