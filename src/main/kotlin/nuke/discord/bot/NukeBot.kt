package nuke.discord.bot

import net.dv8tion.jda.core.JDA
import nuke.discord.command.meta.CommandService
import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

interface NukeBot {

    val config: Config
    val client: JDA

    val commands: CommandService
    val audio: BotAudioState

    fun terminate()

}