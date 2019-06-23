/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.objects;

import me.duncte123.botcommons.commands.ICommandContext;
import me.duncte123.ghostbot.utils.Container;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandEvent implements ICommandContext {

    private final String invoke;
    private final List<String> args;
    private final GuildMessageReceivedEvent event;
    private final Container container;

    public CommandEvent(String invoke, List<String> args, GuildMessageReceivedEvent event, Container container) {
        this.invoke = invoke;
        this.args = args;
        this.event = event;
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }

    public String getInvoke() {
        return this.invoke;
    }

    public List<String> getArgs() {
        return this.args;
    }

    @Override
    public Guild getGuild() {
        return getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }
}
