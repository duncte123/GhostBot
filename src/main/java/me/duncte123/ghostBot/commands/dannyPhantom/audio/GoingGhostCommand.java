package me.duncte123.ghostBot.commands.dannyPhantom.audio;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GoingGhostCommand implements Command {
    private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "goingghost/";
    private final String[] audioFiles = {
            "going ghost 1 (priate radio).mp3",
            "going ghost 2 (priate radio).mp3",
            "going ghost 3 (priate radio).mp3",
            "going ghost 4 (mistery meat).mp3"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if(preAudioChecks(event)) {
            String selectedTrack = audioFiles[SpoopyUtils.random.nextInt(audioFiles.length)];
            if (SpoopyUtils.random.nextInt(100) <= 5) {
                selectedTrack = "its going ghost.mp3";
            }
            sendMsg(event, "Selected track: _" + selectedTrack + "_");
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(), audioPath + selectedTrack, false);
        }
    }

    @Override
    public String getName() {
        return "goingghost";
    }

    @Override
    public Category getCategory() {
        return Category.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in (has a 5% chance of becoming ghostly)";
    }
}
