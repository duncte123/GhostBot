/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan "duncte123" Sterken
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

import me.duncte123.ghostbot.objects.entities.GhostBotApi
import me.duncte123.ghostbot.objects.entities.GhostBotChannel
import me.duncte123.ghostbot.objects.entities.GhostBotGuild
import me.duncte123.ghostbot.objects.entities.GhostBotMessageEvent
import me.duncte123.ghostbot.objects.entities.GhostBotUser

class CommandEvent {

    final String invoke
    final String[] args
    final GhostBotMessageEvent event
    final GhostBotChannel channel
    final GhostBotUser user
    final GhostBotApi api
    final GhostBotGuild guild
    final boolean fromSlack

    CommandEvent(String invoke, String[] args, GhostBotMessageEvent event, boolean fromSlack) {
        this.invoke = invoke
        this.args = args
        this.event = event
        this.channel = event.channel
        this.user = event.author
        this.api = event.API
        this.guild = event.guild
        this.fromSlack = fromSlack
    }
}
