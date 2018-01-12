package nuke.discord.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

/**
 * Holder for both the player and a track scheduler for one guild.
 */
class GuildMusicManager(manager: AudioPlayerManager) {

    val player: AudioPlayer = manager.createPlayer()
    val sendHandler = AudioPlayerSendHandler(player)

    val scheduler = TrackScheduler(player).apply {
        player.addListener(this)
    }

}
