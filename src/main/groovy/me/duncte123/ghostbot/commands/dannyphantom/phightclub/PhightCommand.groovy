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

package me.duncte123.ghostbot.commands.dannyphantom.phightclub

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member

class PhightCommand extends Command {
    @Override
    void execute(CommandEvent event) {

    }

    @Override
    String getName() { 'phight' }

    @Override
    String getHelp() {
        """Makes you "phight" other users
         | Usage: `$Variables.PREFIX$name [user 1] [user 2]`""".stripMargin()
    }

    private static Member getMemberOrNull(String message, Guild guild) {
        def found = FinderUtil.findMembers(message, guild)

        return found[0] ?: null
    }
}
