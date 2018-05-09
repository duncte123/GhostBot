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

package me.duncte123.ghostBot.commands.dannyPhantom.wiki;

import com.afollestad.ason.Ason;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.ghostBot.objects.Command_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import me.duncte123.ghostBot.utils.WikiHolder_java;

/**
 * This class stores objects that are useful to the wiki commands
 */
abstract class WikiBaseCommandJava extends Command_java {

    //shortcut to the wiki
    WikiHolder_java wiki = SpoopyUtils_java.WIKI_HOLDER;

    FandomException toEx(Ason ason) {
        return new FandomException(
                ason.getString("exception.type"),
                ason.getString("exception.message"),
                ason.getInt("exception.code"),
                ason.getString("exception.details"),
                ason.getString("trace_id")
        );
    }
}
