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

import me.duncte123.ghostbot.utils.Container;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class SlashCommandEvent implements ICommandEvent {

    private final String invoke;
    private final List<String> args;
    // TODO: swap with slash command event
    private final GuildMessageReceivedEvent event;
    private final Container container;

    public SlashCommandEvent(String invoke, List<String> args, GuildMessageReceivedEvent event, Container container) {
        this.invoke = invoke;
        this.args = args;
        this.event = event;
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public String getInvoke() {
        return this.invoke;
    }

    @Override
    public List<String> getArgs() {
        return this.args;
    }

    @Override
    public Guild getGuild() {
        return this.event.getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    @Override
    public TextChannel getChannel() {
        return this.event.getChannel();
    }

    @Override
    public Message getMessage() {
        return this.event.getMessage();
    }

    @Override
    public User getAuthor() {
        return this.event.getAuthor();
    }

    @Override
    public Member getMember() {
        return this.event.getMember();
    }

    @Override
    public JDA getJDA() {
        return this.event.getJDA();
    }
}
