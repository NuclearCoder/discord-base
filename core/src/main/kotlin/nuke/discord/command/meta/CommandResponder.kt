package nuke.discord.command.meta

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.SubscribeEvent
import nuke.discord.util.discord.ReactionMenu
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

const val REPLY_SUCCESS = ":white_check_mark:"
const val REPLY_FAILURE = ":negative_squared_cross_mark:"

/**
 * Appends the prefix for a reply.
 * "emote | **name**, "
 */
fun MessageBuilder.replyPrefix(member: Member, emote: String) {
    append(emote)
    append(" | **")
    append(member.effectiveName)
    append("**, ")
}

/**
 * Makes this message a reaction menu.
 */
fun Message.reactionMenu(builder: ReactionMenu.Builder.() -> Unit) {
    ReactionMenu.Builder(this).apply(builder).build()
}

/**
 * Makes this message a choice reaction menu.
 */
fun Message.reactionMenuRange(vararg emotes: String, callback: ReactionMenu.(Member, Int) -> Unit) = reactionMenu {
    emotes.forEachIndexed { i, emote ->
        choice(emote) { callback(member, i) }
    }
}

fun MessageChannel.waitResponse(
        target: Member? = null,
        timeout: Int = 0,
        callback: ResponseObject.(Message) -> Unit) {
    jda.addEventListener(object : ResponseObject(this@waitResponse, target, timeout) {
        override fun response(message: Message) = callback(message)
    })
}

abstract class ResponseObject(
        private val channel: MessageChannel,
        private val target: Member? = null,
        timeout: Int = 10
) {

    companion object {
        val scheduler = ScheduledThreadPoolExecutor(2)
    }

    init {
        scheduler.schedule(::close, timeout.toLong(), TimeUnit.SECONDS)
    }

    @SubscribeEvent
    fun onResponse(event: MessageReceivedEvent) {
        if (event.channel == channel && (target == null || target == event.member)) {
            response(event.message)
        }
    }

    protected abstract fun response(message: Message)

    fun close() {
        channel.jda.removeEventListener(this)
    }

}

