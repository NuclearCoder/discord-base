package nuke.discord.util.discord

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import nuke.discord.LOGGER
import nuke.discord.URL_GET_TIMEOUT
import nuke.discord.command.meta.CommandContext
import nuke.discord.command.meta.command.PermLevel
import nuke.discord.util.use
import java.io.IOException
import java.io.InputStream
import java.net.URL

fun TextChannel.sendImageFromURL(content: String, url: String,
                                 filename: String) {
    try {
        getInputStreamFromUrl(url).use {
            sendFile(it, filename, MessageBuilder().append(content).build()).queue()
        }
    } catch (e: IOException) {
        LOGGER.error("Could not send file:", e)
    }

}

fun getInputStreamFromUrl(url: String): InputStream {
    val conn = URL(url).openConnection()
    conn.doInput = true
    conn.doOutput = true
    conn.readTimeout = URL_GET_TIMEOUT
    conn.connectTimeout = URL_GET_TIMEOUT

    conn.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")

    return conn.getInputStream()
}

fun Member.hasRoleForGuild(roleID: Long): Boolean {
    return this.roles.any { it.idLong == roleID }
}

fun Member.getPermissionLevel(context: CommandContext): nuke.discord.command.meta.command.PermLevel {
    return when {
        user.id == context.botOwner.id -> PermLevel.BOT_OWNER
        isOwner -> PermLevel.SERVER_OWNER
        hasPermission(Permission.KICK_MEMBERS) || hasPermission(Permission.BAN_MEMBERS) -> PermLevel.MODERATOR
        context.event.channelType == ChannelType.PRIVATE -> PermLevel.PRIVATE
        else -> PermLevel.USER
    }
}

fun Member.hasSufficientPermissions(context: CommandContext, desired: nuke.discord.command.meta.command.PermLevel): Boolean {
    return getPermissionLevel(context) >= desired
}

typealias RMCallback = ReactionMenu.(Member) -> Unit