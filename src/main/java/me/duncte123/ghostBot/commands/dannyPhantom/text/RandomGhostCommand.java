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

import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RandomGhostCommand extends Command {

    //private static final Logger logger = LoggerFactory.getLogger(RandomGhostCommand.class);
    private final List<String> ghosts = new ArrayList<>();

    public RandomGhostCommand() {
        logger.info("Scraping ghosts async");
        WebUtils.ins.scrapeWebPage("http://dannyphantom.wikia.com/wiki/Category:Ghosts?display=page&sort=alphabetical").async((doc) -> {
            try {
                Element el = doc.getElementsByClass("mw-content-ltr").get(2);

                Elements tds = el.child(0).child(0).children();

                Elements trs = tds.get(0).children();

                List<Element> anchors = new ArrayList<>();

                trs.forEach(tr ->
                        tr.children().forEach(ul -> {
                            if (ul.tagName().equals("ul")) {
                                ul.children().forEach(listItem ->
                                        anchors.addAll(listItem.children())
                                );
                            }
                        })
                );

                logger.info("Scraped " + anchors.size() + " Ghosts from the wiki");

                anchors.forEach(a -> ghosts.add("http://dannyphantom.wikia.com" + a.attr("href")));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, Throwable::printStackTrace);
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        String ghost = ghosts.get(SpoopyUtils.random.nextInt(ghosts.size()));
        MessageUtils.sendMsg(event, ghost);
    }

    @Override
    public String getName() {
        return "randomghost";
    }

    @Override
    public Category getCategory() {
        return Category.TEXT;
    }

    @Override
    public String getHelp() {
        return "Get a random ghost from the wiki";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ghost"};
    }
}
