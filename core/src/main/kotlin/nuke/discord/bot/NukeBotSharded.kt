package nuke.discord.bot

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.hooks.SubscribeEvent
import nuke.discord.LOGGER
import nuke.discord.command.meta.CommandService
import nuke.discord.command.meta.ResponseObject
import nuke.discord.command.meta.selectors.CommandSelector
import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

@Suppress("UNUSED_PARAMETER")
class NukeBotSharded(override val config: Config,
                     private val commandPrefix: String,
                     commandSelector: CommandSelector,
                     commandBuilder: CommandBuilder,
                     messageHandlers: List<MessageHandler>,
                     listeners: List<Any>,
                     private val shardCount: Int) : NukeBot {

    init {
        LOGGER.info("Starting sharded bot ($shardCount shards)...")
    }

    private val shards = Array(this.shardCount) {
        NukeBotShard(it, commandSelector, commandBuilder, messageHandlers, listeners)
    }

    override val commands: CommandService
        get() = error("Shard manager does not have a command service of its own")

    override val client: JDA
        get() = error("Shard manager does not have a client of its own")

    override val audio = BotAudioState()

    override fun terminate() {
        ResponseObject.scheduler.shutdownNow()

        shards.forEach {
            LOGGER.info("Shutting down shard #${it.shardNo}...")
            it.client.shutdown()
        }

        config.save()
    }

    inner class NukeBotShard(internal val shardNo: Int,
                             commandSelector: CommandSelector,
                             commandBuilder: CommandBuilder,
                             messageHandlers: List<MessageHandler>,
                             listeners: List<Any>)
        : NukeBotBase(config,
            commandPrefix, commandSelector, commandBuilder,
            messageHandlers, listeners) {

        override val client = buildClient {
            useSharding(shardNo, shardCount)
            addEventListener(object {
                @SubscribeEvent
                fun onReady(event: ReadyEvent) {
                    LOGGER.info("Started shard #$shardNo!")
                }
            })
        }

        override val audio = this@NukeBotSharded.audio

        override fun terminate() {
            this@NukeBotSharded.terminate()
        }

    }

}