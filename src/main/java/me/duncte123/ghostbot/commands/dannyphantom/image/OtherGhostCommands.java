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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class OtherGhostCommands extends ImageBase {
    private final ObjectMapper mapper;
    private final Map<String, String[]> keywordsMapped = new LinkedHashMap<>();

    public OtherGhostCommands(ObjectMapper mapper) {
        this.mapper = mapper;
        this.initMap();
    }

    @Override
    public void execute(ICommandEvent event) {
        if (ImageCommand.isReloading) {
            sendMsg(event, "I'm looking for new images on the internet, please be wait.");
            return;
        }

        final String invoke = event.getInvoke();

        if (this.keywordsMapped.containsKey(invoke)) {
            this.sendFromKeywords(event, this.keywordsMapped.get(invoke));
        } else {
            sendMsg(event, "Whut? How did this part of the code even execute?");
            sendMsg(event, "But for real, if this code runs something is 100% broken");
        }
    }

    private void initMap() {
        this.keywordsMapped.put("cujo", new String[]{"Cujo Danny Phantom"});
        this.keywordsMapped.put("ember", new String[]{"Ember Mclain"});
        this.keywordsMapped.put("dan", new String[]{"Dan Phantom", "Dark Danny"});
        this.keywordsMapped.put("vlad", new String[]{"Vlad Plasmius", "Vlad masters"});
        this.keywordsMapped.put("sam", new String[]{"Sam Manson"});
        this.keywordsMapped.put("tucker", new String[]{"Tucker Foley"});
        this.keywordsMapped.put("danny", new String[]{"Danny Fenton", "Danny Phantom"});
        this.keywordsMapped.put("clockwork", new String[]{"Clockwork Danny Phantom"});
        this.keywordsMapped.put("pitchpearl", new String[]{"pitchpearl"});
        this.keywordsMapped.put("valerie", new String[]{"valerie gray"});
        this.keywordsMapped.put("dani", new String[]{"Dani Fenton", "Dani Phantom"});
        this.keywordsMapped.put("skulker", new String[]{"Skulker Danny Phantom"});
        this.keywordsMapped.put("jack", new String[]{"Jack Fenton"});
        this.keywordsMapped.put("jazz", new String[]{"Jazz Fenton"});
        this.keywordsMapped.put("maddie", new String[]{"Maddie Fenton"});
        this.keywordsMapped.put("desiree", new String[]{"Danny Phantom desiree"});
        this.keywordsMapped.put("poindexter", new String[]{"Sidney Poindexter"});
        this.keywordsMapped.put("wes", new String[]{"Wes Weston Danny Phantom"});
    }

    @Override
    public String getName() {
        return "cujo";
    }

    @Override
    public String getHelp() {
        return "Gets a random image for this ghost";
    }

    @Override
    public List<String> getAliases() {
        // yes
        final Set<String> strings = this.keywordsMapped.keySet();

        return new ArrayList<>(strings).subList(1, strings.size());
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.CHARACTERS;
    }

    private void sendFromKeywords(ICommandEvent event, String[] words) {
        sendMessageFromName(event, requestImage(words[ThreadLocalRandom.current().nextInt(words.length)], mapper));
    }
}
