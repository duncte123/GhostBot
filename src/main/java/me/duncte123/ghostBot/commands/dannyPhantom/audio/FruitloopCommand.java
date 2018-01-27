package me.duncte123.ghostBot.commands.dannyPhantom.audio;

import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class FruitloopCommand  extends Command {

    //private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "fruitloop/";
    /*private String[] audioFiles = {
            "fruitloop 1.mp3",
            "fruitloop 2.mp3"
    };*/

    public FruitloopCommand() {
        this.audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "fruitloop/";
        reloadAudioFiles();
    }

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
        return "fruitloop";
    }

    @Override
    public Category getCategory() {
        return Category.AUDIO;
    }

    @Override
    public String getHelp() {
        return "You're one crazed up fruitloop";
    }
}
