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

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.commands.ReactionCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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
            if (args[0].startsWith(PAGE_SELECTOR)) {
                try {
                    page = Integer.parseInt(args[0].substring(PAGE_SELECTOR.length()));
                } catch (NumberFormatException ignored) {
                }
            } else if (args[0].startsWith(CHAPTER_SELECTOR)) {
                try {
                    page = chapters[Integer.parseInt(args[0].substring(CHAPTER_SELECTOR.length())) - 1];
                } catch (NumberFormatException ignored) {
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
        return new String[] {"doppelgangercomic"};
    }

    @Override
    public String getHelp() {
        return "Read the doppelganger comic within discord (comic website: <http://doppelgangercomic.tumblr.com/>)";
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
        String url = String.format(
                "https://api.tumblr.com/v2/blog/%s/posts?api_key=%s&type=%s&limit=20",
                "doppelgangercomic.tumblr.com",
                SpoopyUtils.config.getString("api.tumblr", "API_KEY"),
                "photo"
        );
        WebUtils.ins.getAson(url).async(json -> {
            int total = json.getInt("response.total_posts");
            for (int i = 0; i <= total; i += 20) {
                Ason j = WebUtils.ins.getAson(url + "&offset=" + (i > 1 ? i + 1 : 1)).execute();
                AsonArray<Ason> fetched = j.getJsonArray("response.posts");
                logger.info("Fetched " + fetched.size() + " Pages");
                List<TumblrPost> posts = Ason.deserializeList(fetched, TumblrPost.class)
                        .stream().filter(p -> !filters.contains(p.id)).collect(Collectors.toList());
                /*List<TumblrPost> covers = posts.stream().filter(p -> p.id == 123542277988L).collect(Collectors.toList());
                if(!covers.isEmpty()) {
                    TumblrPost post = covers.get(0);
                    posts.remove(post);
                    post.id = 167255413598L;
                    pages.add(post);
                }*/
                pages.addAll(posts);
            }
            Collections.reverse(pages);
        });
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }
}
