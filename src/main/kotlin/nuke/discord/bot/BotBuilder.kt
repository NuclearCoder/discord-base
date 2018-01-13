package nuke.discord.bot

import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.requests.Requester
import nuke.discord.command.meta.CommandBuilder
import nuke.discord.command.meta.MessageHandler
import nuke.discord.util.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class BotBuilder {

    private var config = Config("nukebot.cfg")

    var configName
        get() = config.filename
        set(value) {
            config = Config(value)
        }

    private var sharded = false
    private var shardCount = -1

    fun sharded() {
        sharded = true
        shardCount = -1
    }

    fun shardedWith(count: Int) {
        if (count < 2) throw IllegalArgumentException("There must be at least two shards")
        sharded = true
        shardCount = count
    }

    private var commandBuilder: CommandBuilder = {}

    fun commands(builder: CommandBuilder) {
        commandBuilder = builder
    }

    private val messageHandlers = mutableListOf<MessageHandler>()

    fun messageHandler(handler: MessageHandler) {
        messageHandlers += handler
    }

    private val listeners = mutableListOf<EventListener>()

    fun eventListener(listener: EventListener) {
        listeners += listener
    }

    fun build(): NukeBot {
        return if (sharded) {
            val actualShardCount = if (shardCount > 1) {
                shardCount
            } else {
                getRecommendedShardCount(config["token"])
            }

            NukeBotSharded(config, commandBuilder, messageHandlers, listeners, actualShardCount)
        } else {
            NukeBotNormal(config, commandBuilder, messageHandlers, listeners)
        }
    }

    private fun getRecommendedShardCount(token: String) =
            Request.Builder().url(Requester.DISCORD_API_PREFIX + "gateway/bot")
                    .header("Authorization", "Bot " + token)
                    .header("User-agent", Requester.USER_AGENT)
                    .build().let { req ->
                OkHttpClient().newCall(req).execute().use {
                    if (!it.isSuccessful) throw IOException("Unexpected code " + it)

                    JSONObject(it.body()!!.string()).getInt("shards")
                }
            }

}