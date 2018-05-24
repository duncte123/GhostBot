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

import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.commands.ReactionCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.utils.TumblrUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class DoppelgangerComicCommand extends ReactionCommand {

    public DoppelgangerComicCommand(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
        loadPages();
    }

    private static final String PROFILE_PICTURE = "https://api.tumblr.com/v2/blog/doppelgangercomic.tumblr.com/avatar/48";

    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";

    private final List<TumblrPost> pages = new ArrayList<>();
    private final int[] chapters = {2, 29, 59};
    private final List<Long> filters = Collections.singletonList(167255413598L);

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        int page = pages.size();
        if (args.length > 0) {
            String arg = StringUtils.join(args).toLowerCase();
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
            sendMsg(event, "I could not find a page with number " + page);
            return;
        }

        AtomicInteger pa = new AtomicInteger(page);
        MessageUtils.sendEmbed(event, getEmbed(pa.get()),
                m -> this.addReactions(m, Arrays.asList(ReactionCommand.LEFT_ARROW, ReactionCommand.RIGHT_ARROW,
                        ReactionCommand.CANCEL), Collections.singleton(event.getAuthor()), 3, TimeUnit.MINUTES, index -> {
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
        TumblrPost post = pages.get(page);
        return EmbedUtils.defaultEmbed()
                .setAuthor("DOPPELGÄNGER", post.post_url, PROFILE_PICTURE)
                .setTitle("Link to post", post.post_url)
                .setDescription(QuotesCommand.parseText(post.caption))
                .setThumbnail(PROFILE_PICTURE)
                .setImage(post.photos.get(0).original_size.url)
                .setTimestamp(null)
                .setFooter(String.format("Page: %s/%s", page + 1, pages.size()), EmbedUtils.FOOTER_ICON)
                .build();
    }

    private void loadPages() {
        if (SpoopyUtils.config.getBoolean("running_local", false)) return;

        pages.clear();
        logger.info("Loading doppelganger pages");
        TumblrUtils.fetchAllFromAccount("doppelgangercomic.tumblr.com", "photo", posts -> {
            List<TumblrPost> posts1 = posts.stream().filter(p -> !filters.contains(p.id)).collect(Collectors.toList());
            //This fetches the new front page that looks better than the old one
            TumblrUtils.fetchSinglePost("doppelgangercomic.tumblr.com", 167255413598L, post -> {
                posts1.set(posts1.size() - 1, post);
                pages.addAll(posts1);
                Collections.reverse(pages);
                logger.info("Loaded " + pages.size() + " pages from the doppelganger comic.");
            });
        });
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }
}
