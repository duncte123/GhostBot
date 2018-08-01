/*
 * GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan "duncte123" Sterken
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

package me.duncte123.ghostBot.objects;

import fredboat.audio.player.LavalinkManager;
import me.duncte123.ghostBot.audio.GuildMusicManager;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public abstract class Command {

    protected final Logger logger;
    protected String audioPath = "";
    private String[] audioFiles = {};
    public Command() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute(String invoke, String[] args, GuildMessageReceivedEvent event);

    public abstract String getName();

    public Category getCategory() {
        return Category.NONE;
    }

    public String[] getAliases() {
        return new String[0];
    }

    public abstract String getHelp();

    public void reloadAudioFiles() {
        if (!getCategory().equals(Category.AUDIO)) return;

        logger.info("Path: " + audioPath);
        File folder = new File(audioPath);
        File[] listOfFiles = folder.listFiles();
        List<String> filesFound = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) return;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                logger.info("File found: " + audioPath + file.getName());
                filesFound.add(file.getName());
            }
        }

        audioFiles = filesFound.toArray(new String[0]);
    }

    protected String getRandomTrack() {
        if (!getCategory().equals(Category.AUDIO)) return null;
        return audioFiles[SpoopyUtils.random.nextInt(audioFiles.length)];
    }

    protected void doAudioStuff(GuildMessageReceivedEvent event) {
        if (!getCategory().equals(Category.AUDIO)) return;

        if (preAudioChecks(event)) {
            String selectedTrack = getRandomTrack();
            sendMsg(event, "Selected track: _" + selectedTrack.replaceAll("_", "\\_") + "_");
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + selectedTrack, false);
        }

    }

    protected GuildMusicManager getMusicManager(Guild guild) {
        return SpoopyUtils.audio.getMusicManager(guild);
    }

    protected boolean preAudioChecks(GuildMessageReceivedEvent event) {
        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            sendEmbed(event, EmbedUtils.embedMessage("Please join a voice channel first"));
            return false;
        }

        try {
            LavalinkManager.ins.openConnection(event.getMember().getVoiceState().getChannel());
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                sendEmbed(event, EmbedUtils.embedMessage(String.format("I don't have permission to join `%s`", event.getMember().getVoiceState().getChannel().getName())));
            } else {
                sendEmbed(event, EmbedUtils.embedMessage(String.format("Error while joining channel `%s`: %s"
                        , event.getMember().getVoiceState().getChannel().getName(), e.getMessage())));
            }
            return false;
        }
        return true;
    }
}
