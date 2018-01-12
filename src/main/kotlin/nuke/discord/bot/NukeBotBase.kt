package nuke.discord.bot

import nuke.discord.music.BotAudioState
import nuke.discord.util.Config

abstract class NukeBotBase(override final val config: Config) : NukeBot {

    override final val audio = BotAudioState()

}