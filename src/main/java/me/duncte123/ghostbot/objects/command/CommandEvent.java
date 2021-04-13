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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class CommandEvent implements ICommandEvent {

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

    @Override
    public Container getContainer() {
        return container;
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
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    @Override
    public void reply(String content) {
        sendMsg(this, content);
    }

    @Override
    public void reply(EmbedBuilder embed) {
        sendEmbed(this, embed);
    }
}
