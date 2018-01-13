package nuke.discord.bot

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.hooks.EventListener
import nuke.discord.LOGGER
import nuke.discord.command.meta.CommandService
import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

class NukeBotSharded(override val config: Config,
                     private val commandPrefix: String,
                     commandBuilder: CommandBuilder,
                     messageHandlers: List<MessageHandler>,
                     listeners: List<EventListener>,
                     private val shardCount: Int) : NukeBot {

    private val shards = Array(this.shardCount) {
        NukeBotShard(it, commandBuilder, messageHandlers, listeners)
    }

    override val commands: CommandService
        get() = error("Shard manager does not have a command service of its own")

    override val client: JDA
        get() = error("Shard manager does not have a client of its own")

    override val audio = BotAudioState()

    override fun terminate() {
        shards.forEach {
            LOGGER.info("Shutting down shard #${it.shardNo}...")
            it.client.shutdown()
        }

        config.save()
    }

    inner class NukeBotShard(internal val shardNo: Int,
                             commandBuilder: CommandBuilder,
                             messageHandlers: List<MessageHandler>,
                             listeners: List<EventListener>)
        : NukeBotBase(config, commandPrefix, commandBuilder, messageHandlers, listeners) {

        override val client = buildClient { useSharding(shardNo, shardCount) }
        override val audio = this@NukeBotSharded.audio

        override fun terminate() {
            this@NukeBotSharded.terminate()
        }

    }

}