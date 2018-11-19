/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.objects

import fredboat.audio.player.LavalinkManager
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.audio.GuildMusicManager
import me.duncte123.ghostbot.utils.AudioUtils
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

abstract class Command extends CommandHelpers {

    protected final Logger logger = LoggerFactory.getLogger(getClass())
    protected String audioPath = ''
    private def audioFiles = []

    abstract void execute(String invoke, String[] args, GuildMessageReceivedEvent event)

    void execute(CommandEvent event) {
        execute(event.invoke, event.args, event.event.originalEvent as GuildMessageReceivedEvent)
    }

    abstract String getName()

    CommandCategory getCategory() {
        return CommandCategory.NONE
    }

    String[] getAliases() {[]}

    abstract String getHelp()

    boolean isSlackCompatible() { false }

    boolean isDiscordCompatible() { true }

    void reloadAudioFiles() {
        if (category != CommandCategory.AUDIO) return

        logger.info("Path: $audioPath")
        def folder = new File(audioPath)
        def listOfFiles = folder.listFiles()
        def filesFound = new ArrayList<String>()

        if (listOfFiles == null || listOfFiles.length == 0) return

        for (File file : listOfFiles) {
            if (file.file) {
                logger.info("File found: $audioPath$file.name")
                filesFound.add(file.name)
            }
        }

        audioFiles = filesFound
    }

    String getRandomTrack() {
        if (category != CommandCategory.AUDIO) return null

        return audioFiles[ThreadLocalRandom.current().nextInt(audioFiles.size())]
    }

    void doAudioStuff(GuildMessageReceivedEvent event) {
        if (category != CommandCategory.AUDIO) return

        if (preAudioChecks(event)) {
            def selectedTrack = randomTrack
            sendMsg(event, "Selected track: _${selectedTrack.replaceAll("_", "\\_")}_")
            AudioUtils.instance.loadAndPlay(getMusicManager(event.guild), event.channel,
                    audioPath + selectedTrack, false)
        }

    }

    static GuildMusicManager getMusicManager(Guild guild) {
        return AudioUtils.instance.getMusicManager(guild)
    }

    static boolean preAudioChecks(GuildMessageReceivedEvent event) {

        def voiceState = event.member.voiceState

        if (!voiceState.inVoiceChannel()) {
            sendEmbed(event, EmbedUtils.embedMessage('Please join a voice channel first'))

            return false
        }

        try {
            LavalinkManager.ins.openConnection(voiceState.channel)
        } catch (PermissionException e) {

            if (e.permission == Permission.VOICE_CONNECT) {
                sendEmbed(event, EmbedUtils.embedMessage("I don't have permission to join $voiceState.channel.name"))
            } else {
                sendEmbed(event, EmbedUtils.embedMessage('Error while joining channel ' +
                        "`$voiceState.channel.name`: $e.message"))
            }

            return false
        }

        return true
    }

}
