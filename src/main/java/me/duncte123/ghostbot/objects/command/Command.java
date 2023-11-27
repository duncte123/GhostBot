/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.objects.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.utils.AudioUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class Command {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Function<String, String> httpPath = (item) -> "https://i.duncte123.me/ghostbotaudio/" + getName() + '/' + item;
    private List<String> audioFiles = new ArrayList<>();

    public abstract void execute(ICommandEvent event);

    public abstract String getName();

    public abstract String getHelp();

    public List<OptionData> getCommandOptions() {
        return List.of();
    }

    public CommandData getCommandData() {
        String parsedHelp = this.getHelp();

        if (parsedHelp == null) {
            throw new IllegalArgumentException(this.getClass() + " is null");
        }

        if (parsedHelp.isEmpty()) {
            throw new IllegalArgumentException(this.getClass() + " is empty");
        }

        if (parsedHelp.contains("Usage")) {
            parsedHelp = parsedHelp.substring(0, parsedHelp.indexOf("Usage"));
        } else if (parsedHelp.contains("usage")) {
            parsedHelp = parsedHelp.substring(0, parsedHelp.indexOf("usage"));
        }

        if (parsedHelp.length() > 100) {
            throw new IllegalArgumentException(this.getClass() + " is over 100");
        }

        return Commands.slash(this.getName(), parsedHelp.trim()).addOptions(this.getCommandOptions());
    }

    public CommandCategory getCategory() {
        return CommandCategory.NONE;
    }

    public List<String> getAliases() {
        return List.of();
    }

    public boolean shouldAck() {
        return false;
    }

    public void shutdown() {
        // May be implemented
    }

    public void reloadAudioFiles() {
        if (getCategory() != CommandCategory.AUDIO) {
            return;
        }

        try {
            final JsonNode audioList = new ObjectMapper().readTree(new File("./data/audioList.json"));

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

    protected boolean preAudioChecks(ICommandEvent event) {

        final GuildVoiceState voiceState = event.getMember().getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply(EmbedUtils.embedMessage("Please join a voice channel first"));

            return false;
        }

        final AudioChannel channel = voiceState.getChannel();
        assert channel != null;

        if (!event.getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            event.reply(EmbedUtils.embedMessage("I don't have permission to join " + channel.getName()));
        }

        try {
            event.getJDA().getDirectAudioController().connect(channel);
        } catch (PermissionException e) {

            if (e.getPermission() == Permission.VOICE_CONNECT) {
                event.reply(EmbedUtils.embedMessage("Somehow got passed the permission check, this should never happen"));
            } else {
                event.reply(EmbedUtils.embedMessage(String.format(
                    "Error while joining channel `%s`: %s",
                    channel.getName(),
                    e.getMessage()
                )));
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    protected void doAudioStuff(ICommandEvent event) {
        doAudioStuff(event, null);
    }

    protected void doAudioStuff(ICommandEvent event, String track) {
        if (getCategory() != CommandCategory.AUDIO) {
            return;
        }

        if (preAudioChecks(event)) {
            final AudioUtils audioUtils = event.getContainer().getAudio();
            final String selectedTrack = track == null ? getRandomTrack() : track;

            event.reply("Selected track: _" + selectedTrack.replace("_", "\\_") + '_');

            audioUtils.loadAndPlay(event.getGuild(), event.getChannel(), this.httpPath.apply(selectedTrack));
        }
    }
}
