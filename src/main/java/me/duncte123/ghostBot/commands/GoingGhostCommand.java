package me.duncte123.ghostBot.commands;

import me.duncte123.ghostBot.objects.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GoingGhostCommand implements Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        sendMsg(event, "soon\\\u2122");
    }

    @Override
    public String getName() {
        return "goingghost";
    }

    @Override
    public String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in";
    }
}
