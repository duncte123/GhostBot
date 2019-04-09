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

package me.duncte123.ghostbot.commands.dannyphantom.text

import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.objects.tumblr.TumblrPost
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.utils.TumblrUtils
import me.duncte123.ghostbot.variables.Variables

import java.util.concurrent.ThreadLocalRandom

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg
import static me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand.parseText

class AuCommand extends Command {

    //https://reallydumbdannyphantomaus.tumblr.com
    private final String domain = 'reallydumbdannyphantomaus.tumblr.com'
    private final List<TumblrPost> allAus = []
    private final TLongObjectMap<List<TumblrPost>> guildAus = new TLongObjectHashMap<>()

    AuCommand() {
        loadAus()
    }

    @Override
    void execute(CommandEvent commandEvent) {

        def args = commandEvent.args
        def event = commandEvent.event

        if (args.length == 1 && args[0] == 'reload' && event.author.get().idLong == Variables.OWNER_ID) {
            loadAus()
            sendMsg(event, 'Reloading')
            return
        }

        if (allAus.isEmpty()) {
            sendMsg(event, 'No AU\'s found, they are probably being reloaded')
            return
        }

        def gid = event.guild.idLong

        if (!guildAus.containsKey(gid) || guildAus.get(gid).isEmpty()) {
            guildAus.put(gid, new ArrayList<>(allAus))
        }

        def posts = guildAus.get(gid)
        def post = posts[ThreadLocalRandom.current().nextInt(posts.size())]

        posts.remove(post)

        def tags = post.tags.join(' #')

        def eb = EmbedUtils.defaultEmbed()
            .setTitle('Link to Post', post.post_url)
            .setDescription(parseText(post.body))
            .setFooter("#$tags", Variables.FOOTER_ICON)
            .setTimestamp(null)

        sendEmbed(event, eb)
    }

    @Override
    String getName() { 'au' }

    @Override
    java.util.List<String> getAliases() {
        [
            'reallydumbdannyphantomaus',
            'dumbau',
            'greatau',
            'reallygreatdannyphantomaus'
        ]
    }

    @Override
    String getHelp() { 'Shows you a really great DP AU.' }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.TEXT
    }

    private void loadAus() {
        if (SpoopyUtils.config.running_local) {
            return
        }

        allAus.clear()

        def tagToFind1 = 'dpau'
        def tagToFind2 = 'reallybaddpau'

        logger.info('Loading the best aus ever')

        TumblrUtils.instance.fetchAllFromAccount(domain, 'text') {

            def filtered = it.stream().filter {
                p -> p.tags.contains(tagToFind1) && p.tags.contains(tagToFind2)
            }.collect()

            allAus.addAll(filtered)

            logger.info("Loaded ${allAus.size()} aus")
        }
    }
}
