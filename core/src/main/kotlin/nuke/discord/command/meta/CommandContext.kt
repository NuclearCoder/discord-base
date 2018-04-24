package nuke.discord.command.meta

import com.thatsnomoon.kda.extensions.sendAsync
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import nuke.discord.bot.NukeBot
import nuke.discord.util.discord.MessageTokenizer

class CommandContext(val event: MessageReceivedEvent,
                     val bot: NukeBot,
                     val botOwner: User,
                     val message: Message,
                     val command: String,
                     val tokenizer: MessageTokenizer) {

    inline fun reply(emote: String = REPLY_SUCCESS, crossinline then: MessageBuilder.() -> Unit) {
        event.channel.sendAsync {
            replyPrefix(event.member, emote)
            then()
        }
    }

    fun reply(emote: String, content: String) = reply(emote) { append(content) }

    fun reply(content: String) = reply(REPLY_SUCCESS, content)
    fun replyFail(content: String) = reply(REPLY_FAILURE, content)

    fun replyMissingArguments(details: String) = reply(REPLY_FAILURE) {
        append("missing arguments: ")
        append(details)
    }

    val jda get(): JDA = message.jda

    val author get(): User = message.author
    val member get(): Member = message.member

    val guild get(): Guild = message.guild
    val category get(): Category = message.category
    val channel get(): MessageChannel = message.channel

    val mentionedChannels get(): List<MessageChannel> = message.mentionedChannels
    val mentionedMembers get(): List<Member> = message.mentionedMembers
    val mentionedRoles get(): List<Role> = message.mentionedRoles
    val mentionedUsers get(): List<User> = message.mentionedUsers

}