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

package me.duncte123.fandomApi.endpoints;

import me.duncte123.fandomApi.endpoints.search.List;
import me.duncte123.fandomApi.endpoints.user.Details;
import me.duncte123.fandomApi.models.FandomResult;

public class Endpoints {

    public static class Search {
        public FandomResult list(String query) {
            return new List(query).execute();
        }

        public FandomResult list(String query, int batch) {
            return new List(query, batch).execute();
        }
    }

    public static class User {
        public FandomResult details(String ids) {
            return new Details(ids).execute();
        }

        public FandomResult details(String ids, int size) {
            return new Details(ids, size).execute();
        }
    }
}

