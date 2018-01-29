package me.duncte123.ghostBot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.ghostBot.audio.GuildMusicManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class AudioUtils {

    private static final int DEFAULT_VOLUME = 35; //(0-150, where 100 is the default max volume)

    private final String embedTitle = "Spoopy-Luma-Player";

    public final String BASE_AUDIO_DIR = "./audioFiles/";

    private final AudioPlayerManager playerManager;

    private final Map<String, GuildMusicManager> musicManagers;

    AudioUtils() {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(playerManager);

        musicManagers = new HashMap<>();
    }

    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void loadAndPlay(GuildMusicManager mng, final TextChannel channel, final String trackUrlRaw, final boolean addPlayList) {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (trackUrlRaw.startsWith("<") && trackUrlRaw.endsWith(">")) {
            trackUrl = trackUrlRaw.substring(1, trackUrlRaw.length() - 1);
        } else {
            trackUrl = trackUrlRaw;
        }

        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                /*String msg = "Adding to queue: " + track.getInfo().title;
                if (mng.player.getPlayingTrack() == null) {
                    msg += "\nand the Player has started playing;";
                }

                sendEmbed(EmbedUtils.embedField(embedTitle, msg), channel);*/
                //Stop any playing tracks
                if (mng.player.getPlayingTrack() != null) {
                    mng.player.stopTrack();
                }
                mng.scheduler.queue(track);

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                //String msg;

                if (addPlayList) {
                    /*msg = "Adding **" + playlist.getTracks().size() + "** tracks to queue from playlist: " + playlist.getName();
                    if (mng.player.getPlayingTrack() == null) {
                        msg += "\nand the Player has started playing;";
                    }*/
                    tracks.forEach(mng.scheduler::queue);
                } else {
                    /*msg = "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")";
                    if (mng.player.getPlayingTrack() == null) {
                        msg += "\nand the Player has started playing;";
                    }*/
                    mng.scheduler.queue(firstTrack);
                }
                //sendEmbed(EmbedUtils.embedField(embedTitle, msg), channel);
            }

            @Override
            public void noMatches() {
                sendEmbed(EmbedUtils.embedField(embedTitle, "Nothing found by _" + trackUrl + "_"), channel);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sendEmbed(EmbedUtils.embedField(embedTitle, "Could not play: " + exception.getMessage() + "\nIf this happens often try another link or join our [support guild](https://discord.gg/NKM9Xtk) for more!"), channel);
            }
        });
    }

    public synchronized GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId);
                if (mng == null) {
                    mng = new GuildMusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }

        guild.getAudioManager().setSendingHandler(mng.getSendHandler());

        return mng;
    }

    private void sendEmbed(MessageEmbed embed, TextChannel tc) {
        if (tc.canTalk()) {
            if (!tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_EMBED_LINKS)) {
                tc.sendMessage(EmbedUtils.embedToMessage(embed)).queue();
                return;
            }
            tc.sendMessage(embed).queue();
        }
    }

    public Map<String, GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }
}