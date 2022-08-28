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
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.utils.TumblrUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand.parseText;

public class AuCommand extends Command {
    //https://reallydumbdannyphantomaus.tumblr.com
//    private final List<TumblrPost> allAus = new ArrayList<>();
//    private final TLongObjectMap<List<TumblrPost>> guildAus = new TLongObjectHashMap<>();

    @Override
    public void execute(ICommandEvent event) {
        /*final List<String> args = event.getArgs();

        if (args.size() == 1 && "reload".equalsIgnoreCase(args.get(0)) && event.getAuthor().getIdLong() == Variables.OWNER_ID) {
            // make sure to delete the file
            loadCachedAus(event.getContainer().getConfig(), event.getContainer().getJackson());
            event.reply("Reloading");

            return;
        }*/

        final List<TumblrPost> aus = this.getAUs(event.getContainer());

        if (aus.isEmpty()) {
            event.reply("No AU's found, they are probably being reloaded");

            return;
        }

        /*final long gid = event.getGuild().getIdLong();

        if (!guildAus.containsKey(gid) || guildAus.get(gid).isEmpty()) {
            guildAus.put(gid, new ArrayList<>(allAus));
        }*/

//        final List<TumblrPost> posts = guildAus.get(gid);
        final TumblrPost post = aus.get(ThreadLocalRandom.current().nextInt(aus.size()));

//        posts.remove(post);

        final String tags = String.join(" #", post.tags);

        final EmbedBuilder eb = EmbedUtils.getDefaultEmbed()
            .setTitle("Click to view original post", post.post_url)
            .setDescription(parseText(post.body))
            .setFooter("#" + tags, Variables.FOOTER_ICON)
            .setTimestamp(null);

        event.reply(eb);
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

    public List<TumblrPost> getAUs(Container container) {
        final File ausFile = new File("./data/aus.json");

        if (!ausFile.exists()) {
            this.loadAus(container.getConfig(), container.getJackson(), ausFile);
            return List.of();
        }

        try {
            return container.getJackson().readValue(ausFile, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAus(GhostBotConfig config, ObjectMapper jackson, File ausFile) {
        final String tagToFind1 = "dpau";
        final String tagToFind2 = "reallybaddpau";
        final String domain = "reallydumbdannyphantomaus.tumblr.com";

        logger.info("Loading the best aus ever");

        TumblrUtils.getInstance().fetchAllFromAccount(domain, "text", config, jackson, (it) -> {
            final List<TumblrPost> filtered = it.stream().filter(
                (p) -> {
                    final List<String> tags = Arrays.asList(p.tags);

                    return tags.contains(tagToFind1) && tags.contains(tagToFind2);
                }
            ).toList();

            try {
                if (!ausFile.createNewFile()) {
                    logger.error("Failed to create aus file");

                    return;
                }

                try (final BufferedWriter writer = new BufferedWriter(new FileWriter(ausFile, StandardCharsets.UTF_8))) {
                    writer.write(jackson.writeValueAsString(filtered));
                }

                logger.info("Wrote aus to file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
