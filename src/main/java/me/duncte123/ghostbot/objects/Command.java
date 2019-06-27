/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostbot.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fredboat.audio.player.LavalinkManager;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.utils.AudioUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public abstract class Command {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Function<String, String> httpPath = (item) -> "https://i.duncte123.me/ghostbotaudio/" + getName() + '/' + item;
    private List<String> audioFiles = new ArrayList<>();

    public abstract void execute(CommandEvent event);

    public abstract String getName();

    public abstract String getHelp();

    public CommandCategory getCategory() {
        return CommandCategory.NONE;
    }

    public List<String> getAliases() {
        return List.of();
    }

    public void shutdown() {
        // May be implemented
    }

    public void reloadAudioFiles() {
        if (getCategory() != CommandCategory.AUDIO) {
            return;
        }

        try {
            final JsonNode audioList = new ObjectMapper().readTree(new File("audioList.json"));

            if (!audioList.has(getName())) {
                return;
            }

            logger.info("Path: {}", getName());

            final List<String> filesFound = new ArrayList<>();

            for (final JsonNode name : audioList.get(getName())) {
                logger.info("File found: {}/{}", getName(), name.asText());
                filesFound.add(name.asText());
            }

            audioFiles = filesFound;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getRandomTrack() {
        if (getCategory() != CommandCategory.AUDIO) {
            return null;
        }

        return audioFiles.get(
            ThreadLocalRandom.current().nextInt(audioFiles.size())
        );
    }

    protected boolean preAudioChecks(CommandEvent event) {

        final GuildVoiceState voiceState = event.getMember().getVoiceState();

        if (voiceState == null || !voiceState.inVoiceChannel()) {
            sendEmbed(event, EmbedUtils.embedMessage("Please join a voice channel first"));

            return false;
        }

        final VoiceChannel channel = voiceState.getChannel();
        assert channel != null;

        try {
            LavalinkManager.ins.openConnection(channel);
        } catch (PermissionException e) {

            if (e.getPermission() == Permission.VOICE_CONNECT) {
                sendEmbed(event,
                    EmbedUtils.embedMessage("I don't have permission to join " + channel.getName())
                );
            } else {
                sendEmbed(event, EmbedUtils.embedMessage(String.format(
                    "Error while joining channel `%s`: %s",
                    channel.getName(),
                    e.getMessage()
                )));
            }

            return false;
        }

        return true;
    }

    protected GuildMusicManager getMusicManager(AudioUtils audio, Guild guild) {
        return audio.getMusicManager(guild);
    }

    protected void doAudioStuff(CommandEvent event) {

        if (getCategory() != CommandCategory.AUDIO) {
            return;
        }

        if (preAudioChecks(event)) {
            final AudioUtils audioUtils = event.getContainer().getAudio();
            final String selectedTrack = getRandomTrack();

            sendMsg(event, "Selected track: _" + selectedTrack.replaceAll("_", "\\\\_") + '_');

            audioUtils.loadAndPlay(getMusicManager(audioUtils, event.getGuild()), event.getChannel(),
                this.httpPath.apply(selectedTrack));
        }

    }
}
