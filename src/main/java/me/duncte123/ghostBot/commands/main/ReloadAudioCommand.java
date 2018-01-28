package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Set;

public class ReloadAudioCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if(!event.getAuthor().getId().equals(Variables.OWNER_ID)) return;

        SpoopyUtils.commandManager.getCommands().forEach(Command::reloadAudioFiles);

        sendSuccess(event.getMessage());

        //noinspection ConstantConditions
        if(true) return;


    /////////////////////////////////// Meme Code //////////////////////////////////////////

        CommandManager commandManager = SpoopyUtils.commandManager;
        Set<Command> commands = commandManager.getCommands();
        for(Command cmd : commands) {
            if(cmd.getCategory().equals(Category.AUDIO))
                cmd.reloadAudioFiles();
        }

        System.out.println("this should never run");
    }

    @Override
    public String getName() {
        return "reloadaudio";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Category getCategory() {
        return Category.HIDDEN;
    }
}
