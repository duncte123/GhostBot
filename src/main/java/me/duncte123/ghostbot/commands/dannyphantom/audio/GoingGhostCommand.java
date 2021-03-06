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

package me.duncte123.ghostbot.commands.dannyphantom.audio;

import me.duncte123.ghostbot.objects.command.ICommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GoingGhostCommand extends AudioBaseCommand {
    @Override
    public void execute(ICommandEvent event) {
        final int p = ThreadLocalRandom.current().nextInt(100);

        if (p >= 50 && p <= 55) {
            doAudioStuff(event, "extra/its_going_ghost.mp3");

            return;
        }

        doAudioStuff(event);
    }

    @Override
    public String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in (has a 5% chance of becoming ghostly)";
    }
}
