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

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class DoppelgangerComicCommand extends TumblrComicBase {

    public DoppelgangerComicCommand(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
        this.blogUrl = "doppelgangercomic.tumblr.com";
        this.filename = "doppelganger.json";
        this.chapters = new int[]{
            2,   // Chapter 1
            29,  // Chapter 2
            59,  // Chapter 3
            82,  // Chapter 4
            104, // Chapter 5
            127, // Chapter 6
        };

        loadPages();
    }

    @Override
    public String getName() {
        return "doppelganger";
    }

    @Override
    public String getHelp() {
        return "Read the doppelganger comic within discord (comic website: <http://doppelgangercomic.tumblr.com/>)\n" +
            "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("doppelgangercomic");
    }

    @NotNull
    @Override
    MessageEmbed getEmbed(int page) {
        final TumblrPost post = pages.get(page);

        return EmbedUtils.defaultEmbed()
            .setAuthor("DOPPELGÄNGER", post.post_url, getProfilePicture())
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
        return null;
    }
}
