package nuke.discord.bot.basic

import nuke.discord.LOGGER
import nuke.discord.bot.CommandBuilder
import nuke.discord.bot.NukeBotBase
import nuke.discord.bot.buildClient
import nuke.discord.command.meta.CommandService
import nuke.discord.util.Config

class NukeBotBasic(config: Config, builder: CommandBuilder) : NukeBotBase(config) {
    init {
        LOGGER.info("Starting unsharded bot...")
    }

    override val commands = CommandService(this, builder)
    override val client = buildClient()

    override fun terminate() {
        LOGGER.info("Shutting down...")

        client.shutdown()

        config.save()
    }
}