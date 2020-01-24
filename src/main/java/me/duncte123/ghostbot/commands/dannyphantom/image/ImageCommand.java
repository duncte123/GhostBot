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

import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.utils.ConfigUtils;
import me.duncte123.ghostbot.variables.Variables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class ImageCommand extends ImageBase {
    private final String[] keywords = {
        "Danny Phantom",
        "Danny Fenton",
        "Danny Fenton",
        "Samantha Manson",
        "Sam Manson",
        "Tucker Foley",
        "Jack Fenton",
        "Maddie Fenton",
        "Jazz Fenton",
        "Vlad Plasmius",
        "Danny Fenton (Danny Phantom)",
        "Sam Manson (Danny Phantom)",
        "Tucker Foley (Danny Phantom)",
        "Jack Fenton (Danny Phantom)",
        "Maddie Fenton (Danny Phantom)",
        "Jazz Fenton (Danny Phantom)",
        "Vlad Masters (Danny Phantom)",
        "Vlad Plasmius (Danny Phantom)",
        "Danny Fenton",
        "Danny Phantom desiree"
    };
    static boolean isReloading = false;

    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();

        if (!args.isEmpty() && args.get(0).equals("reload") && event.getAuthor().getIdLong() == Variables.OWNER_ID) {
            reloadImages(
                event,
                args.size() > 1 && args.get(1).equals("pretty")
            );

            return;
        }

        if (isReloading) {
            sendMsg(event, "I'm looking for new images on the internet, please be wait.");
            return;
        }

        final String keyword = keywords[ThreadLocalRandom.current().nextInt(keywords.length)];
        final ImageData file = requestImage(keyword, event.getContainer().getJackson());

        sendMessageFromName(event, file);
    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom <:DPEmblemInvertStroke:402746292788264960> related image from google";
    }

    private void reloadImages(CommandEvent event, boolean pretty) {
        isReloading = true;

        final File jarFile = new File("ghostBotImages.jar");
        final String className = "me.duncte123.ghostBotImages.ImageScraper2";

        try {
            final URL fileURL = jarFile.toURI().toURL();
            final String jarURL = "jar:" + fileURL + "!/";
            final URL[] urls = {new URL(jarURL)};
            final URLClassLoader ucl = new URLClassLoader(urls);

            Class.forName(className, true, ucl)
                .getDeclaredConstructor(Boolean.TYPE)
                .newInstance(pretty);

            IMAGES = new ConfigUtils().getImages();

            sendMsg(event, "done reloading");
        } catch (MalformedURLException | InstantiationException | IllegalAccessException |
            ClassNotFoundException | NoSuchMethodException | InvocationTargetException ex) {
            ex.printStackTrace();
            sendMsg(event, "An error occurred: " + ex.getMessage());
        } finally {
            isReloading = false;
        }
    }

}
