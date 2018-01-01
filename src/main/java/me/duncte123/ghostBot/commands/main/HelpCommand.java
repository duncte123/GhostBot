package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.Show;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            String toSearch = StringUtils.join(args, " ");
            if(toSearch.startsWith(Variables.PREFIX))
                toSearch = toSearch.replaceFirst(Variables.PREFIX, "");

            for (Command cmd : SpoopyUtils.commandManager.getCommands()) {
                if (cmd.getName().equals(toSearch)) {
                    sendMsg(event, "Command help for `" + cmd.getName() + "` :\n" + cmd.getHelp() + (cmd.getAliases().length > 0 ? "\nAliases: " + StringUtils.join(cmd.getAliases(), ", ") : ""));
                    return;
                } else {
                    for (String alias : cmd.getAliases()) {
                        if (alias.equals(toSearch)) {
                            sendMsg(event, "Command help for `" + cmd.getName() + "` :\n" + cmd.getHelp() + (cmd.getAliases().length > 0 ? "\nAliases: " + StringUtils.join(cmd.getAliases(), ", ") : ""));
                            return;
                        }

                    }

                }
            }

            sendMsg(event, "That command could not be found, try " + Variables.PREFIX + "help for a list of commands");
            return;
        }

        List<String> dannyPhantomCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c->c.getShow().equals(Show.DANNY_PHANTOM)).map(Command::getName).collect(Collectors.toList());
        List<String> otherCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c->c.getShow().equals(Show.NONE)).map(Command::getName).collect(Collectors.toList());

        MessageEmbed helpEmbed = EmbedUtils.defaultEmbed()
                .addField("Danny Phantom commands",
                        "`" + Variables.PREFIX + StringUtils.join(dannyPhantomCommands, "`\n`" + Variables.PREFIX ) + "`", false)
                .addField("Other commands", "`" + Variables.PREFIX + StringUtils.join(otherCommands, "`\n`" + Variables.PREFIX ) + "`", false)
                .build();

        event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(helpEmbed).queue(
                    msg -> sendMsg(event, event.getAuthor().getAsMention() + ", check your dms"),
                    error -> sendEmbed(event, helpEmbed)
            ),
                error -> sendEmbed(event, helpEmbed)
        );
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows a list of all the commands";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"commands"};
    }
}
