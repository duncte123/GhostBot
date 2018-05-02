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

package me.duncte123.ghostBot.objects.googleSearch;

public class SearchItem {
    private String kind;
    private String title;
    private String htmlTitle;
    private String link;
    private String displayLink;
    private String snippet;
    private String htmlSnippet;
    private String mime;
    private ImageData image;

    public SearchItem() {}

    public String getTitle() {
        return title;
    }

    public String getDisplayLink() {
        return displayLink;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public ImageData getImage() {
        return image;
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public String getKind() {
        return kind;
    }

    public String getLink() {
        return link;
    }

    public String getMime() {
        return mime;
    }

    public String getSnippet() {
        return snippet;
    }

}
