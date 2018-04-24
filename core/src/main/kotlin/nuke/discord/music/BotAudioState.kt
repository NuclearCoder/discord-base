package nuke.discord.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.core.entities.Guild
import nuke.discord.command.meta.CommandContext
import java.util.concurrent.ConcurrentHashMap

class BotAudioState {

    private val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()

    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().apply {
        registerSourceManager(YoutubeAudioSourceManager())
    }

    fun loadAndPlay(manager: GuildMusicManager, context: CommandContext, trackUrl: String) {
        playerManager.loadItemOrdered(manager, trackUrl, object : AudioLoadResultHandler {

            override fun trackLoaded(track: AudioTrack) {
                manager.scheduler.queue(track)
                context.reply("music started!")
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                // DO NOT USE PLAYLISTS PLEASE
                //val tracks = playlist.tracks
                //var firstTrack = playlist.selectedTrack ?: tracks[0]
            }

            override fun noMatches() {

            }

            override fun loadFailed(exception: FriendlyException) {
                context.replyFail("an error occurred loading the music.")
            }
        })
    }

    fun getMusicManager(guild: Guild): GuildMusicManager {
        return musicManagers.computeIfAbsent(guild.idLong) {
            GuildMusicManager(playerManager).apply { player.volume = 100 }
        }
    }

}