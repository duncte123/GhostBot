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

package me.duncte123.ghostbot.commands.dannyphantom.text;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGhostCommand extends Command {
    private final String wikiUrl = "https://dannyphantom.fandom.com";

    @Override
    public void execute(ICommandEvent event) {
        final List<String> ghosts = this.getGhosts(event.getContainer().getJackson());

        if (ghosts.isEmpty()) {
            event.reply("It looks like Danny defeated all the ghosts");
            return;
        }

        final String ghost = ghosts.get(ThreadLocalRandom.current().nextInt(ghosts.size()));

        event.reply(wikiUrl + ghost);
    }

    @Override
    public String getName() {
        return "randomghost";
    }

    @Override
    public String getHelp() {
        return "Get a random ghost from the wiki";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ghost");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TEXT;
    }

    public List<String> getGhosts(ObjectMapper mapper) {

        final File ghostsFile = new File("./data/wikiGhosts.json");

        if (ghostsFile.exists()) {
            try {
                return mapper.readValue(ghostsFile, new TypeReference<>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        final List<String> tmpGhosts = new ArrayList<>();
        final Document document = WebUtils.ins.scrapeWebPage(wikiUrl + "/wiki/Category:Ghosts?display=page&sort=alphabetical").execute();
        final Elements els = document.getElementsByClass("category-page__member");
        final List<Element> anchors = new ArrayList<>();

        els.forEach((el) -> {
            final Elements aTags = el.getElementsByClass("category-page__member-link");

            anchors.addAll(aTags);
        });

        logger.info("Scraped {} Ghosts from the wiki", anchors.size());

        anchors.forEach((a) -> tmpGhosts.add(a.attr("href")));

        try {
            if (ghostsFile.createNewFile()) {
                try (final BufferedWriter writer = new BufferedWriter(new FileWriter(ghostsFile, StandardCharsets.UTF_8))) {
                    writer.write(mapper.writeValueAsString(tmpGhosts));
                }

                logger.info("Wrote ghosts to file");
            } else {
                logger.error("Failed to create ghosts file");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tmpGhosts;
    }
}
