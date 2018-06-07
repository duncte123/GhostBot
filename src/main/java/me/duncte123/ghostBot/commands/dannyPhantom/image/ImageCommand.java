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

import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.utils.ConfigUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ImageCommand extends ImageBase {

    private final String[] keywords = {
            "Danny Phantom",
            "pitch pearl",
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
            "Danny Fenton"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if(args.length > 0 && args[0].equals("reload") && event.getAuthor().getId().equals(Variables.OWNER_ID) && !isReloading) {
            isReloading = true;
            new Thread(() -> {
                File jarFile = new File("ghostBotImages.jar");
                String className = "me.duncte123.ghostBotImages.ImageScraper2";
                boolean pretty = false;
                if(args.length > 1 && args[1].equals("-pretty-print"))
                    pretty = true;
                try {
                    URL fileURL = jarFile.toURI().toURL();
                    String jarURL = "jar:" + fileURL + "!/";
                    URL urls[] = {new URL(jarURL)};
                    URLClassLoader ucl = new URLClassLoader(urls);
                    Class.forName(className, true, ucl).getDeclaredConstructor(Boolean.TYPE)
                            .newInstance(pretty);
                    SpoopyUtils.IMAGES = new ConfigUtils().loadImages();
                    isReloading = false;
                    MessageUtils.sendMsg(event, "done reloading");
                } catch (MalformedURLException | InstantiationException | IllegalAccessException |
                        ClassNotFoundException | NoSuchMethodException | InvocationTargetException ex) {
                    ex.printStackTrace();
                    MessageUtils.sendMsg(event, "An error occurred: " + ex.getMessage());
                    isReloading = false;
                }
            }).start();
            return;
        }

        if(!isReloading) {
            String keyword = keywords[SpoopyUtils.random.nextInt(keywords.length)];
            System.out.println(keyword);
            ImageData file = requestImage(keyword);
            sendMessageFromName(event, file);
        } else {
            MessageUtils.sendMsg(event, "I'm looking for new images on the internet, please be patient.");
        }
    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom <:DPEmblemInvertStroke:402746292788264960> related image from google";
    }
}
