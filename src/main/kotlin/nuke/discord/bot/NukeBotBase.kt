package nuke.discord.bot

import club.minnced.kjda.client
import club.minnced.kjda.plusAssign
import club.minnced.kjda.token
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.hooks.AnnotatedEventManager
import net.dv8tion.jda.core.hooks.EventListener
import nuke.discord.command.meta.CommandBuilder
import nuke.discord.command.meta.CommandService
import nuke.discord.command.meta.MessageHandler
import nuke.discord.util.Config

@Suppress("LeakingThis")
abstract class NukeBotBase(override val config: Config,
                           commandBuilder: CommandBuilder,
                           messageHandlers: List<MessageHandler>,
                           private val listeners: List<EventListener>) : NukeBot {

    override val commands = CommandService(this, commandBuilder, messageHandlers)

    protected fun buildClient(preInit: JDABuilder.() -> Unit = {}): JDA = client(AccountType.BOT) {
        token { config["token"] }

        preInit()

        setEventManager(AnnotatedEventManager())
        this += commands
        listeners.forEach {
            this += it
        }
    }


}