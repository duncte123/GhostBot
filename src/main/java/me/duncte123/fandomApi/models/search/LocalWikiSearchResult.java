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

package me.duncte123.fandomApi.models.search;

@SuppressWarnings("unused")
public class LocalWikiSearchResult {

    private int quality;
    private String url;
    private int ns;
    private int id;
    private String title;
    private String snippet;

    public LocalWikiSearchResult(int quality, String url, int ns, int id, String title, String snippet) {
        this.quality = quality;
        this.url = url;
        this.ns = ns;
        this.id = id;
        this.title = title;
        this.snippet = snippet;
    }

    public LocalWikiSearchResult() {
    }

    public int getId() {
        return id;
    }

    public int getNs() {
        return ns;
    }

    public int getQuality() {
        return quality;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
