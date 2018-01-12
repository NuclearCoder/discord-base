package nuke.discord.bot.sharded

import net.dv8tion.jda.core.JDA
import nuke.discord.LOGGER
import nuke.discord.bot.CommandBuilder
import nuke.discord.bot.NukeBotBase
import nuke.discord.bot.getRecommendedShardCount
import nuke.discord.command.meta.CommandService
import nuke.discord.util.Config

class NukeBotSharded(config: Config, shardCount: Int = 0, builder: CommandBuilder) : NukeBotBase(config) {

    init {
        LOGGER.info("Starting sharded bot...")
    }

    private val shardCount = if (shardCount > 0) shardCount else getRecommendedShardCount()
    private val shards = Array(this.shardCount) { NukeBotShard(this, it, this.shardCount, builder) }

    override val commands: CommandService
        get() = error("Shard manager does not have a command service of its own")

    override val client: JDA
        get() = error("Shard manager does not have a client of its own")

    override fun terminate() {
        shards.forEach {
            LOGGER.info("Shutting down shard #${it.shardId}...")
            it.client.shutdown()
        }

        config.save()
    }

}
