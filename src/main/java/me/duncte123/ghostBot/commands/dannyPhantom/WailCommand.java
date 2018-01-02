package me.duncte123.ghostBot.commands.dannyPhantom;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class WailCommand implements Command {

    private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "wail/";
    private final String[] audioFiles = {
            "ghost wail 1.mp3",
            "ghost wail 2.mp3",
            "ghost wail 3.mp3",
            "ghost wail 4.mp3",
            "ghost wail 5.mp3",
            "ghost wail 6.mp3"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if(preAudioChecks(event)) {
            String selectedTrack = audioFiles[SpoopyUtils.random.nextInt(audioFiles.length)];
            sendMsg(event, "Selected track: _" + selectedTrack + "_");
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + selectedTrack, false);
        }
    }

    @Override
    public String getName() {
        return "wail";
    }

    @Override
    public Category getCategory() {
        return Category.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Gives you a nice ghostly wail";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"ghostlywail"};
    }
}
