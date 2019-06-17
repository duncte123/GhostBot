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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class TheelectricundeadCommand extends TumblrComicBase {
    private final TLongList badPosts = new TLongArrayList(new long[]{
        183827754213L,
    });

    public TheelectricundeadCommand(CommandManager.ReactionListenerRegistry registry, Container container) {
        super(registry);
        this.filename = "bzzt.json";
        this.blogUrl = "theelectricundead.tumblr.com";
        this.chapters = new int[]{
            1,  // Chapter 1
            14, // Chapter 2
        };
        loadPages(container);
    }

    @NotNull
    @Override
    MessageEmbed getEmbed(int page) {
        final TumblrPost post = pages.get(page);

        return EmbedUtils.defaultEmbed()
            .setAuthor("Bzzt", post.post_url, getProfilePicture())
            .setColor(0x29AD2D)
            .setTitle("Link to post", post.post_url)
            .setDescription(QuotesCommand.parseText(post.caption))
            .setThumbnail(getProfilePicture())
            .setImage(post.photos.get(0).getOriginalSize().getUrl())
            .setTimestamp(null)
            .setFooter(String.format("Page: %s/%s", page + 1, pages.size()), Variables.FOOTER_ICON)
            .build();
    }

    @Override
    Predicate<TumblrPost> getFilter() {
        return (post) -> !badPosts.contains(post.id) &&
            post.source_url == null && // source_url is set when it's a reblog
            !post.caption.contains("blockquote") &&
            post.summary != null &&
            (post.summary.contains("Page") || post.summary.contains("Chapter"));
    }

    @Override
    public String getName() {
        return "theelectricundead";
    }

    @Override
    public List<String> getAliases() {
        return List.of("bzzt");
    }

    @Override
    public String getHelp() {
        return "Read the electric undead comic within discord (comic website: <http://theelectricundead.tumblr.com/>)\n" +
            "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }
}
