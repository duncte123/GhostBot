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

import fredboat.audio.player.LavalinkManager;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.utils.AudioUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public abstract class Command {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected String audioPath = "";
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

    public void reloadAudioFiles() {
        if (getCategory() != CommandCategory.AUDIO) {
            return;
        }

        logger.info("Path: {}", audioPath);
        final File folder = new File(audioPath);
        final File[] listOfFiles = folder.listFiles();
        final List<String> filesFound = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) {
            return;
        }

        for (final File file : listOfFiles) {
            if (file.isFile()) {
                final String name = file.getName();

                logger.info("File found: {}{}", audioPath, name);
                filesFound.add(name);
            }
        }

        audioFiles = filesFound;
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

        if (!voiceState.inVoiceChannel()) {
            sendEmbed(event, EmbedUtils.embedMessage("Please join a voice channel first"));

            return false;
        }

        try {
            LavalinkManager.ins.openConnection(voiceState.getChannel());
        } catch (PermissionException e) {

            if (e.getPermission() == Permission.VOICE_CONNECT) {
                sendEmbed(event,
                    EmbedUtils.embedMessage("I don't have permission to join " + voiceState.getChannel().getName())
                );
            } else {
                sendEmbed(event, EmbedUtils.embedMessage(String.format(
                    "Error while joining channel `%s`: %s",
                    voiceState.getChannel().getName(),
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
                audioPath + selectedTrack, false);
        }

    }
}
