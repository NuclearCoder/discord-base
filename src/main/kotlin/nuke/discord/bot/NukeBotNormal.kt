package nuke.discord.bot

import net.dv8tion.jda.core.hooks.EventListener
import nuke.discord.LOGGER
import nuke.discord.command.meta.CommandBuilder
import nuke.discord.command.meta.MessageHandler
import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

class NukeBotNormal(config: Config,
                    commandBuilder: CommandBuilder,
                    messageHandlers: List<MessageHandler>,
                    listeners: List<EventListener>)
    : NukeBotBase(config, commandBuilder, messageHandlers, listeners) {

    override val client = buildClient()

    override val audio = BotAudioState()

    override fun terminate() {
        LOGGER.info("Shutting down...")
        client.shutdown()

        config.save()
    }

}