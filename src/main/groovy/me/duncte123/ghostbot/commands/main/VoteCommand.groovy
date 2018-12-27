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

package me.duncte123.ghostbot.commands.main

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandEvent

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed

class VoteCommand extends Command {

    @Override
    void execute(CommandEvent event) {

        def message = '''\
Help the bot grow bigger by upvoting it on one of these sites:
https://botsfordiscord.com/bots/397297702150602752/vote
https://discordbots.org/bot/397297702150602752/vote
'''
        sendEmbed(event.event, EmbedUtils.embedMessage(message))
    }

    @Override
    String getName() { 'vote' }

    @Override
    String getHelp() { 'Shows a list of places where you can vote for the bot' }
}
