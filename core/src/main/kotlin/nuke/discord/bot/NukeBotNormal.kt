package nuke.discord.bot

import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.hooks.SubscribeEvent
import nuke.discord.LOGGER
import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

class NukeBotNormal(config: Config,
                    commandPrefix: String,
                    commandBuilder: CommandBuilder,
                    messageHandlers: List<MessageHandler>,
                    listeners: List<Any>)
    : NukeBotBase(config, commandPrefix, commandBuilder, messageHandlers, listeners) {

    init {
        LOGGER.info("Starting unsharded bot...")
    }

    override val client = buildClient {
        addEventListener(object {
            @SubscribeEvent
            fun onReady(event: ReadyEvent) {
                LOGGER.info("Started unsharded bot!")
            }
        })
    }

    override val audio = BotAudioState()

    override fun terminate() {
        LOGGER.info("Shutting down...")
        client.shutdown()

        config.save()
    }

}