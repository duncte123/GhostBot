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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OtherGhostCommands extends ImageBase {
    private final ObjectMapper mapper;

    public OtherGhostCommands(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void execute(CommandEvent event) {

        switch (event.getInvoke()) {
            case "cujo":
                sendFromKeywords(event, "Cujo Danny Phantom");
                break;
            case "ember":
                sendFromKeywords(event, "Ember Mclain");
                break;
            case "dan":
                sendFromKeywords(event, "Dan Phantom", "Dark Danny");
                break;
            case "vlad":
                sendFromKeywords(event, "Vlad Plasmius", "Vlad masters");
                break;
            case "sam":
                sendFromKeywords(event, "Sam Manson");
                break;
            case "tucker":
                sendFromKeywords(event, "Tucker Foley");
                break;
            case "danny":
                sendFromKeywords(event, "Danny Fenton", "Danny Phantom");
                break;
            case "clockwork":
                sendFromKeywords(event, "Clockwork Danny Phantom");
                break;
            case "pitchpearl":
                sendFromKeywords(event, "pitchpearl");
                break;
            case "valerie":
                sendFromKeywords(event, "valerie gray");
                break;
            case "dani":
                sendFromKeywords(event, "Dani Fenton", "Dani Phantom");
                break;
            case "skulker":
                sendFromKeywords(event, "Skulker Danny Phantom");
                break;
            case "jack":
                sendFromKeywords(event, "Jack Fenton");
                break;
            case "jazz":
                sendFromKeywords(event, "Jazz Fenton");
                break;
            case "maddie":
                sendFromKeywords(event, "Maddie Fenton");
                break;
            case "desiree":
                sendFromKeywords(event, "Danny Phantom desiree");
                break;
            case "poindexter":
                sendFromKeywords(event, "Sidney Poindexter");
                break;
            case "wes":
                sendFromKeywords(event, "Wes Weston Danny Phantom");
                break;
        }

    }

    @Override
    public String getName() {
        return
            "cujo";
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ember", "dan", "vlad",
            "sam", "tucker", "danny", "clockwork", "pitchpearl", "valerie", "dani", "skulker", "jack", "jazz",
            "maddie", "desiree", "poindexter", "wes");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.CHARACTERS;
    }

    private void sendFromKeywords(CommandEvent event, String... words) {
        sendMessageFromName(event, requestImage(words[ThreadLocalRandom.current().nextInt(words.length)], mapper));
    }
}
