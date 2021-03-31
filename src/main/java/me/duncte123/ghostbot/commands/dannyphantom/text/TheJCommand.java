/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2019  Duncan "duncte123" Sterken
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

import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.ICommandEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class TheJCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        final String message = String.format("The J in Daniel J Fenton stands for \"%s\"", this.getRandomJ());

        sendMsg(event, message);
    }

    @Override
    public String getName() {
        return "thej";
    }

    @Override
    public String getHelp() {
        return "What does the J in \"Daniel J Fenton\" stand for?";
    }

    private String getRandomJ() {
        try {
            final File nameList = new File("./data/nameList.txt");
            List<String> content = Files.readAllLines(nameList.toPath());

            return content.get((int)Math.floor(Math.random() * content.size()));
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
