package nuke.discord.bot

import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.requests.Requester
import nuke.discord.command.meta.registry.CommandRegistry
import nuke.discord.command.meta.selectors.CommandSelector
import nuke.discord.command.meta.selectors.ExactSelector
import nuke.discord.util.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

typealias CommandBuilder = (CommandRegistry.RegistryBuilder) -> Unit
typealias MessageHandler = (MessageReceivedEvent) -> Unit

class BotBuilder {

    var configName = "nukebot.cfg"

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

    private var commandPrefix = "!"
    private var commandSelector: CommandSelector = ExactSelector
    private var commandBuilder: CommandBuilder = {}

    fun commands(prefix: String = "!",
                 selector: CommandSelector = ExactSelector,
                 builder: CommandBuilder) {
        if (prefix.contains("\\w".toRegex()))
            throw IllegalArgumentException("The command prefix cannot contain any whitespace")
        commandPrefix = prefix
        commandSelector = selector
        commandBuilder = builder
    }

    private val messageHandlers = mutableListOf<MessageHandler>()

    fun messageHandler(handler: MessageHandler) {
        messageHandlers += handler
    }

    private val listeners = mutableListOf<Any>()

    fun eventListener(listener: Any) {
        listeners += listener
    }

    fun build(): NukeBot {
        val config = Config(configName)
        return if (sharded) {
            val actualShardCount = if (shardCount > 1) {
                shardCount
            } else {
                getRecommendedShardCount(config["token"])
            }

            NukeBotSharded(
                    config,
                    commandPrefix, commandSelector, commandBuilder,
                    messageHandlers, listeners,
                    actualShardCount
            )
        } else {
            NukeBotNormal(
                    config,
                    commandPrefix, commandSelector, commandBuilder,
                    messageHandlers, listeners
            )
        }
    }

    private fun getRecommendedShardCount(token: String) =
            Request.Builder().url(Requester.DISCORD_API_PREFIX + "gateway/bot")
                    .header("Authorization", "Bot $token")
                    .header("User-agent", Requester.USER_AGENT)
                    .build().let { req ->
                OkHttpClient().newCall(req).execute().use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $it")

                    JSONObject(it.body()!!.string()).getInt("shards")
                }
            }

}