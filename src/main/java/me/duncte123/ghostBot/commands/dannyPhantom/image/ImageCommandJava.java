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

import me.duncte123.ghostBot.objects.Category_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils_java.sendMsg;

public class ImageCommandJava extends ImageBase_java {

    private final String[] keywords = {
            "Danny Phantom",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "pitch pearl",
            "Danny Fenton",
            "Samantha Manson",
            "Sam Manson",
            "Tucker Foley",
            "Jack Fenton",
            "Maddy Fenton",
            "Jazz Fenton",
            "Vlad Plasmius",
            "Danny Fenton (Danny Phantom)",
            "Sam Manson (Danny Phantom)",
            "Tucker Foley (Danny Phantom)",
            "Jack Fenton (Danny Phantom)",
            "Maddy Fenton (Danny Phantom)",
            "Jazz Fenton (Danny Phantom)",
            "Vlad Masters (Danny Phantom)",
            "Vlad Plasmius (Danny Phantom)",
            "Danny Fenton"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        sendMsg(event, "Loading....", msg -> {
            String keyword = keywords[SpoopyUtils_java.random.nextInt(keywords.length)];

            String file = requestImage(keyword);
            sendMessageFromName(msg, file, keyword);

            /*requestSearch(keyword,
                    data -> sendMessageFromData(msg, data, keyword),
                    er -> sendMsg(event, "Error while looking up image: " + er));*/

        });
    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public Category_java getCategory() {
        return Category_java.IMAGE;
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom <:DPEmblemInvertStroke:402746292788264960> related image from google";
    }
}
