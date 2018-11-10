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

package me.duncte123.ghostbot.commands.dannyphantom.text

import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.utils.SpoopyUtils
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.jsoup.nodes.Element

import java.util.concurrent.ThreadLocalRandom

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class RandomGhostCommand extends Command {

    private final List<String> ghosts = []
    private final String wikiUrl = "https://dannyphantom.fandom.com"

    RandomGhostCommand() {
        if (!SpoopyUtils.config.running_local) {
            return
        }

        logger.info("Scraping ghosts async")

        WebUtils.ins.scrapeWebPage("$wikiUrl/wiki/Category:Ghosts?display=page&sort=alphabetical").async {

            def els = it.getElementsByClass('category-page__member')
            def anchors = new ArrayList<Element>()

            els.forEach {
                def aTags = it.getElementsByClass('category-page__member-link')
                anchors.addAll(aTags)
            }

            logger.info("Scraped ${anchors.size()} Ghosts from the wiki")

            anchors.forEach {
                ghosts.add(it.attr("href"))
            }

        }

    }

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (ghosts.empty) {
            sendMsg(event, "It looks like Danny defeated all the ghosts")
            return
        }

        def ghost = ghosts[ThreadLocalRandom.current().nextInt(ghosts.size())]
        sendMsg(event, "$wikiUrl$ghost")
    }

    @Override
    String getName() { "randomghost" }

    @Override
    String getHelp() { "Get a random ghost from the wiki" }

    @Override
    String[] getAliases() { ["ghost"] }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.TEXT
    }
}
