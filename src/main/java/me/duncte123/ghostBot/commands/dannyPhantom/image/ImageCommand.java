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

package me.duncte123.ghostBot.commands.dannyPhantom.image;

import me.duncte123.ghostBot.objects.CommandCategory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class ImageCommand extends ImageBase {

    private final String[] keywords = {
            "Danny Phantom",
            "Danny Fenton",
            "Danny Fenton",
            "Samantha Manson",
            "Sam Manson",
            "Tucker Foley",
            "Jack Fenton",
            "Maddie Fenton",
            "Jazz Fenton",
            "Vlad Plasmius",
            "Danny Fenton (Danny Phantom)",
            "Sam Manson (Danny Phantom)",
            "Tucker Foley (Danny Phantom)",
            "Jack Fenton (Danny Phantom)",
            "Maddie Fenton (Danny Phantom)",
            "Jazz Fenton (Danny Phantom)",
            "Vlad Masters (Danny Phantom)",
            "Vlad Plasmius (Danny Phantom)",
            "Danny Fenton"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        String keyword = keywords[ThreadLocalRandom.current().nextInt(keywords.length)];
        System.out.println(keyword);
        ImageData file = requestImage(keyword);
        sendMessageFromName(event, file);

    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom <:DPEmblemInvertStroke:402746292788264960> related image from google";
    }
}
