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

import com.google.gson.reflect.TypeToken;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.messaging.MessageUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostBot.commands.ReactionCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.TumblrUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.ghostBot.utils.SpoopyUtils.newLongSet;

public class DoppelgangerComicCommand extends ReactionCommand {

    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";
    private static final String BLOG_URL = "doppelgangercomic.tumblr.com";
    private static final String PROFILE_PICTURE = "https://api.tumblr.com/v2/blog/" + BLOG_URL + "/avatar/48";
    private final List<TumblrPost> pages = new ArrayList<>();
    // The numbers in this list represent the page numbers of where the chapters start
    private final int[] chapters = {
            2,   // Chapter 1
            29,  // Chapter 2
            59,  // Chapter 3
            82,  // Chapter 4
            104, // Chapter 5
            127  // Chapter 6
    };

    public DoppelgangerComicCommand(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
        loadPages();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (pages.isEmpty()) {
            sendMsg(event, "Something went wrong with loading the pages, please notify duncte123#1245");
            return;
        }

        int page = 0;
        if (args.length > 0) {
            String arg = String.join("", args).toLowerCase();

            if (arg.startsWith(PAGE_SELECTOR)) {
                page = getNumberFromArg(arg.substring(PAGE_SELECTOR.length()));
            } else if (arg.startsWith(CHAPTER_SELECTOR)) {

                try {
                    page = chapters[getNumberFromArg(arg.substring(CHAPTER_SELECTOR.length())) - 1];
                } catch (IndexOutOfBoundsException ignored) {
                    sendMsg(event, "That chapter is not known");
                    return;
                }
            }
        }

        if (page > 0) {
            page--;
        }

        if (page > pages.size()) {
            sendMsg(event, "I could not find a page with number " + (page + 1));
            return;
        }

        AtomicInteger pa = new AtomicInteger(page);
        MessageUtils.sendMsg(event,
                new MessageBuilder()
                        .append("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n")
                        .append("The controls have a timeout of 30 minutes")
                        .setEmbed(getEmbed(pa.get()))
                        .build(),
                m -> this.addReactions(m, Arrays.asList(ReactionCommand.LEFT_ARROW, ReactionCommand.RIGHT_ARROW,
                        ReactionCommand.CANCEL), newLongSet(event.getAuthor().getIdLong()), 30, TimeUnit.MINUTES, index -> {
                            if (index >= 2) { //cancel button or other error
                                stopReactions(m);
                                return;
                            }
                            int nextPage = pa.updateAndGet(current -> index == 1 ? Math.min(current + 1, pages.size() - 1) : Math.max(current - 1, 0));
                            m.editMessage(getEmbed(nextPage)).queue();
                        }
                )
        );

    }

    @Override
    public String getName() {
        return "doppelganger";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"doppelgangercomic"};
    }

    @Override
    public String getHelp() {
        return "Read the doppelganger comic within discord (comic website: <http://doppelgangercomic.tumblr.com/>)\n" +
                "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }

    private int getNumberFromArg(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return pages.size();
        }
    }

    private MessageEmbed getEmbed(int page) {
        final TumblrPost post = pages.get(page);
        return EmbedUtils.defaultEmbed()
                .setAuthor("DOPPELGÄNGER", post.post_url, PROFILE_PICTURE)
                .setTitle("Link to post", post.post_url)
                .setDescription(QuotesCommand.parseText(post.caption))
                .setThumbnail(PROFILE_PICTURE)
                .setImage(post.photos.get(0).original_size.url)
                .setTimestamp(null)
                .setFooter(String.format("Page: %s/%s", page + 1, pages.size()), Variables.FOOTER_ICON)
                .build();
    }

    private void loadPages() {
        pages.clear();
        logger.info("Loading doppelganger pages");

        try {
            List<TumblrPost> posts = TumblrUtils.getInstance()
                    .getGson().fromJson(
                            new FileReader("doppelganger.json"),
                            new TypeToken<List<TumblrPost>>() {
                            }.getType());

            pages.addAll(posts);
            logger.info("Loaded " + pages.size() + " pages from the doppelganger comic.");
        } catch (FileNotFoundException e) {
            logger.error("Failed to load doppelganer pages", e);
        }

    }


    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }
}
