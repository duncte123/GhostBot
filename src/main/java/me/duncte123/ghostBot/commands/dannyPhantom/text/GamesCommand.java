/*
 * GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostBot.commands.dannyPhantom.text;

import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.botcommons.messaging.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GamesCommand implements Command {

    private final String[] games = {
            "https://paurachan.deviantart.com/art/Danny-Phantom-Dress-up-game-v0-1-435498005",
            "http://www.dannyphantomgames.net/fright-flight.php",
            "http://www.dannyphantomgames.net/dueling-decks.php",
            "http://www.dannyphantomgames.net/action-jack.php",
            "http://www.dannyphantomgames.net/urban-jungle-rumble.php",
            "http://www.dannyphantomgames.net/the-ultimate-enemy-face-off.php",
            "http://www.dannyphantomgames.net/portal-peril.php",
            "http://www.dannyphantomgames.net/freak-for-all.php",
            "http://www.dannyphantomgames.net/prom-fright.php",
            "http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-action-jack/qjm29h",
            "http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-fright-flight/rp6an6",
            "http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-enemy-face-off/g11t0x",
            "http://www.nick.com.au/shows/dannyphantom/games/dannyphantom-freak-for-all/yk6kly"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        String game = games[ThreadLocalRandom.current().nextInt(games.length)];
        MessageUtils.sendMsg(event, game);
    }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getHelp() {
        return "Gives you a Danny Phantom game.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TEXT;
    }
}
