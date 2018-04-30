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

package me.duncte123.fandomApi.models.user;

import me.duncte123.fandomApi.models.FandomResult;

import java.util.List;

public class UserResultSet implements FandomResult {

    private String basepath;
    private List<UserElement> items;

    public UserResultSet(String basePath, List<UserElement> items) {
        this.basepath = basePath;
        this.items = items;
    }


    public UserResultSet() {
    }

    public List<UserElement> getItems() {
        return items;
    }

    public String getBasepath() {
        return basepath;
    }
}
