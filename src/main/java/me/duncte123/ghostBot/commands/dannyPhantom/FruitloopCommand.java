package me.duncte123.ghostBot.commands.dannyPhantom;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.Show;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class FruitloopCommand implements Command {
    private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "fruitloop/";
    private final String[] audioFiles = {
            "fruitloop 1.mp3",
            "fruitloop 2.mp3"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if(preAudioChecks(event)) {
            sendSuccess(event.getMessage());
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + audioFiles[SpoopyUtils.random.nextInt(audioFiles.length)], false);
        }
    }

    @Override
    public String getName() {
        return "fruitloop";
    }

    @Override
    public Show getShow() {
        return Show.DANNY_PHANTOM;
    }

    @Override
    public String getHelp() {
        return "You're one crazed up fruitloop";
    }
}
