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

import me.duncte123.ghostBot.audio.GuildMusicManager;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.duncte123.ghostBot.utils.MessageUtils.*;

public abstract class Command {
    protected String audioPath = "";
    protected String[] audioFiles = {};

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

        System.out.println(getName() + ": " + audioPath);
        File folder = new File(audioPath);
        File[] listOfFiles = folder.listFiles();
        List<String> filesFound = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) return;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(getName() + ": " + audioPath + file.getName());
                filesFound.add(file.getName());
            }
        }

        audioFiles = filesFound.toArray(new String[0]);
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
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                sendMsg(event, EmbedUtils.embedMessage(String.format("I don't have permission to join `%s`", event.getMember().getVoiceState().getChannel().getName())));
            } else {
                sendMsg(event, EmbedUtils.embedMessage(String.format("Error while joining channel `%s`: %s"
                        , event.getMember().getVoiceState().getChannel().getName(), e.getMessage())));
            }
            return false;
        }
        return true;
    }
}
