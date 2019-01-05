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

package me.duncte123.ghostbot.objects


import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class CommandEvent {

    final String invoke
    final String[] args
    final GuildMessageReceivedEvent event
    final TextChannel channel
    final User author
    final JDA api
    final Guild guild
    final Message message
    final Member selfMember

    CommandEvent(String invoke, String[] args, GuildMessageReceivedEvent event) {
        this.invoke = invoke
        this.args = args
        this.event = event
        this.channel = event.channel
        this.author = event.author
        this.api = event.JDA
        this.guild = event.guild
        this.message = event.message
        this.selfMember = event.guild.selfMember
    }
}
