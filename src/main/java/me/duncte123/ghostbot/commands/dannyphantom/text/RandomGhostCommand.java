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

import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class RandomGhostCommand extends Command {
    private final List<String> ghosts = new ArrayList<>();
    private final String wikiUrl = "https://dannyphantom.fandom.com";

    public RandomGhostCommand(GhostBotConfig config) {
        if (config.running_local) {
            return;
        }

        logger.info("Scraping ghosts async");

        WebUtils.ins.scrapeWebPage(wikiUrl + "/wiki/Category:Ghosts?display=page&sort=alphabetical").async((it) -> {
            final Elements els = it.getElementsByClass("category-page__member");
            final List<Element> anchors = new ArrayList<>();

            els.forEach((el) -> {
                final Elements aTags = el.getElementsByClass("category-page__member-link");

                anchors.addAll(aTags);
            });

            logger.info("Scraped {} Ghosts from the wiki", anchors.size());

            anchors.forEach((a) -> ghosts.add(a.attr("href")));
        });

    }

    @Override
    public void execute(ICommandEvent event) {

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
}
