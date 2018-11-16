/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan 'duncte123' Sterken
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

package me.duncte123.ghostbot.commands.dannyphantom.text

import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import java.util.concurrent.ThreadLocalRandom

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class GamesCommand extends Command {

    private final String[] games = [
            'https://paurachan.deviantart.com/art/Danny-Phantom-Dress-up-game-v0-1-435498005',
            'https://dpgames.duncte123.me/fright-flight.html',
            'https://dpgames.duncte123.me/dueling-decks.html',
            'https://dpgames.duncte123.me/action-jack.html',
            'https://dpgames.duncte123.me/urban-jungle-rumble.html',
            'https://dpgames.duncte123.me/the-ultimate-enemy-face-off.html',
            'https://dpgames.duncte123.me/portal-peril.html',
            'https://dpgames.duncte123.me/freak-for-all.html',
            'https://dpgames.duncte123.me/prom-fright.html',
            'http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-action-jack/qjm29h',
            'http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-fright-flight/rp6an6',
            'http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-enemy-face-off/g11t0x',
            'http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-freak-for-all/yk6kly'
    ]

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        def game = games[ThreadLocalRandom.current().nextInt(games.length)]

        sendMsg(event, "Here is a DP game: $game\n" +
                'The game will work best on an old browser like internet explorer because it has flash enabled')
    }

    @Override
    String getName() { 'game' }

    @Override
    String getHelp() { 'Gives you a Danny Phantom game.' }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.TEXT
    }
}
