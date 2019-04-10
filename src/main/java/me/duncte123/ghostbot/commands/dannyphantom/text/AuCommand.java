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

package me.duncte123.ghostbot.commands.dannyphantom.text;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.utils.TumblrUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand.parseText;

public class AuCommand extends Command {
    //https://reallydumbdannyphantomaus.tumblr.com
    private final List<TumblrPost> allAus = new ArrayList<>();
    private final TLongObjectMap<List<TumblrPost>> guildAus = new TLongObjectHashMap<>();

    public AuCommand() {
        loadAus();
    }

    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();

        if (args.size() == 1 && "reload".equalsIgnoreCase(args.get(0)) && event.getAuthor().getIdLong() == Variables.OWNER_ID) {
            loadAus();
            sendMsg(event, "Reloading");

            return;
        }

        if (allAus.isEmpty()) {
            sendMsg(event, "No AU's found, they are probably being reloaded");

            return;
        }

        final long gid = event.getGuild().getIdLong();

        if (!guildAus.containsKey(gid) || guildAus.get(gid).isEmpty()) {
            guildAus.put(gid, new ArrayList<>(allAus));
        }

        final List<TumblrPost> posts = guildAus.get(gid);
        final TumblrPost post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

        posts.remove(post);

        final String tags = String.join(" #", post.tags);

        final EmbedBuilder eb = EmbedUtils.defaultEmbed()
            .setTitle("Link to Post", post.post_url)
            .setDescription(parseText(post.body))
            .setFooter("#" + tags, Variables.FOOTER_ICON)
            .setTimestamp(null);

        sendEmbed(event, eb);
    }

    @Override
    public String getName() {
        return "au";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "reallydumbdannyphantomaus",
            "dumbau",
            "greatau",
            "reallygreatdannyphantomaus"
        );
    }

    @Override
    public String getHelp() {
        return "Shows you a really great DP AU.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TEXT;
    }

    private void loadAus() {
        if (SpoopyUtils.getConfig().running_local) {
            return;
        }

        allAus.clear();

        final String tagToFind1 = "dpau";
        final String tagToFind2 = "reallybaddpau";
        final String domain = "reallydumbdannyphantomaus.tumblr.com";

        logger.info("Loading the best aus ever");

        TumblrUtils.getInstance().fetchAllFromAccount(domain, "text", (it) -> {

            final List<TumblrPost> filtered = it.stream().filter(
                (p) -> {
                    final List<String> tags = Arrays.asList(p.tags);

                    return tags.contains(tagToFind1) && tags.contains(tagToFind2);
                }
            ).collect(Collectors.toList());

            allAus.addAll(filtered);

            logger.info("Loaded {} aus", allAus.size());
        });
    }
}
