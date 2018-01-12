package nuke.discord.bot

import club.minnced.kjda.client
import club.minnced.kjda.plusAssign
import club.minnced.kjda.token
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.hooks.AnnotatedEventManager
import net.dv8tion.jda.core.requests.Requester
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

internal fun NukeBot.getRecommendedShardCount() =
        Request.Builder().url(Requester.DISCORD_API_PREFIX + "gateway/bot")
                .header("Authorization", "Bot " + config["token"])
                .header("User-agent", Requester.USER_AGENT)
                .build().let { req ->
            OkHttpClient().newCall(req).execute().use {
                if (!it.isSuccessful) throw IOException("Unexpected code " + it)

                JSONObject(it.body()!!.string()).getInt("shards")
            }
        }

internal fun NukeBot.buildClient(preInit: JDABuilder.() -> Unit = {}): JDA = client(AccountType.BOT) {
    token { config["token"] }

    preInit()

    setEventManager(AnnotatedEventManager())

    this += commands
}
