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

package me.duncte123.fandomapi.user

class UserElement {

    final String name
    final String avatar
    final String url
    final int userId
    final int numberofedits
    final String title

    String basePath

    UserElement(String name, String avatar, String relativeUrl, int userId, int numberofedits, String title, String basePath) {
        this.name = name
        this.avatar = avatar
        this.url = relativeUrl
        this.userId = userId
        this.numberofedits = numberofedits
        this.title = title
        this.basePath = basePath
    }

    String getAbsoluteUrl() {
        basePath + url
    }
}
