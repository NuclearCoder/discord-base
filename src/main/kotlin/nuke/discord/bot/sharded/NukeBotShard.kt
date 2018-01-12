package nuke.discord.bot.sharded

import nuke.discord.LOGGER
import nuke.discord.bot.CommandBuilder
import nuke.discord.bot.NukeBot
import nuke.discord.bot.buildClient
import nuke.discord.command.meta.CommandService

class NukeBotShard(val bot: NukeBot, val shardId: Int, val shardCount: Int, builder: CommandBuilder) : NukeBot by bot {

    init {
        LOGGER.info("Starting shard #$shardId...")
    }

    override val commands = CommandService(this, builder)
    override val client = buildClient { useSharding(shardId, shardCount) }

}
