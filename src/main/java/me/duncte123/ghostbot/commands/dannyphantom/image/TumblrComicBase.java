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

import com.fasterxml.jackson.core.type.TypeReference;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.ReactionCommand;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.ghostbot.utils.SpoopyUtils.newLongSet;

abstract class TumblrComicBase extends ReactionCommand {
    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";
    final List<TumblrPost> pages = new ArrayList<>();
    int[] chapters;
    String filename;
    String blogUrl;

    TumblrComicBase(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
    }

    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();
        final User author = event.getAuthor();

        if (pages.isEmpty()) {
            sendMsg(event, "Something went wrong with loading the pages, please notify duncte123#1245");
            return;
        }

        int page = 0;

        if (args.size() > 0) {
            final String arg = String.join("", args).toLowerCase();

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

        final AtomicInteger pa = new AtomicInteger(page);

        sendMsg(event,
            new MessageBuilder()
                .append("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n")
                .append("The controls have a timeout of 30 minutes")
                .setEmbed(getEmbed(pa.get()))
                .build(),
            (it) -> this.addReactions(it, LEFT_RIGHT_CANCEL, newLongSet(author.getIdLong()), 30, TimeUnit.MINUTES,
                (index) -> {
                    if (index >= 2) { //cancel button or other error
                        stopReactions(it);

                        return;
                    }

                    final int nextPage = pa.updateAndGet((current) -> index == 1 ? Math.min(current + 1, pages.size() - 1) : Math.max(current - 1, 0));

                    it.editMessage(getEmbed(nextPage)).queue();
                })
        );
    }

    private int getNumberFromArg(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return pages.size();
        }
    }

    String getProfilePicture() {
        return "https://api.tumblr.com/v2/blog/" + blogUrl + "/avatar/48";
    }

    @Nonnull
    abstract MessageEmbed getEmbed(int page);

    void loadPages() {
        final String clsName = getClass().getSimpleName();

        logger.info("Loading {} pages", clsName);

        final File comicFile = new File(filename);

        if (comicFile.exists()) {
            List<TumblrPost> posts = null;
            try {
                posts = SpoopyUtils.getJackson().readValue(comicFile, new TypeReference<List<TumblrPost>>() {});
            } catch (IOException e) {
                logger.error("Failed to load " + clsName + " comic", e);
            }

            if (posts != null) {
                pages.addAll(posts);
                logger.info("Loaded {} pages from the {} comic.", pages.size(), clsName);
            }
        }
    }
}
