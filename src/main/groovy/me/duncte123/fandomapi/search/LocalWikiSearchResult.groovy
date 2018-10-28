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

package me.duncte123.fandomapi.search

class LocalWikiSearchResult {

    final int quality
    final String url
    final int ns
    final int id
    final String title
    final String snippet

    LocalWikiSearchResult(int quality, String url, int ns, int id, String title, String snippet) {
        this.quality = quality
        this.url = url
        this.ns = ns
        this.id = id
        this.title = title
        this.snippet = snippet
    }
}
