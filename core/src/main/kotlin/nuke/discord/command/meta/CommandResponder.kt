package nuke.discord.command.meta

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.SubscribeEvent
import nuke.discord.util.discord.ReactionMenu

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

fun MessageChannel.waitResponse(target: Member? = null, callback: ResponseObject.(Message) -> Unit) {
    jda.addEventListener(object : ResponseObject {
        @SubscribeEvent
        fun onResponse(event: MessageReceivedEvent) {
            if (event.channel == this@waitResponse && (target == null || target == event.member)) {
                callback(event.message)
            }
        }

        override fun close() {
            jda.removeEventListener(this)
        }
    })
}

interface ResponseObject {
    fun close()
}