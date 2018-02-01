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

public class UserElement {

    private final String name;
    private final String avatar;
    private final String relativeUrl;
    private final int userId;
    private final int numberofedits;
    private final String title;

    private final String basePath;

    public UserElement(String name, String avatar, String relativeUrl, int userId, int numberofedits, String title, String basePath) {
        this.name = name;
        this.avatar = avatar;
        this.relativeUrl = relativeUrl;
        this.userId = userId;
        this.numberofedits = numberofedits;
        this.title = title;
        this.basePath = basePath;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public String getAbsoluteUrl() {
        return basePath + relativeUrl;
    }

    public int getUserId() {
        return userId;
    }

    public int getNumberofedits() {
        return numberofedits;
    }

    public String getTitle() {
        return title;
    }
}
