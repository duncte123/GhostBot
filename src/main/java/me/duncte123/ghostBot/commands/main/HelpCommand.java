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
        List<String> dannyPhantomCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c->c.getShow().equals(Show.DANNY_PHANTOM)).map(Command::getName).collect(Collectors.toList());
        List<String> otherCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c->c.getShow().equals(Show.NONE)).map(Command::getName).collect(Collectors.toList());

        MessageEmbed message = EmbedUtils.defaultEmbed()
                .addField("Danny Phantom commands",
                        "`" + Variables.PREFIX + StringUtils.join(dannyPhantomCommands, "`\n`" + Variables.PREFIX ) + "`", false)
                .addField("Other commands", "`" + Variables.PREFIX + StringUtils.join(otherCommands, "`\n`" + Variables.PREFIX ) + "`", false)
                .build();

        event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue(
                    msg -> sendMsg(event, event.getAuthor().getAsMention() + ", check your dms"),
                    error -> sendEmbed(event, message)
            ),
                error -> sendEmbed(event, message)
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
