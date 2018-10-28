/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.objects.fyl

class FylChapter {

    final String pageId
    final int pages
    final String name
    final String chapterUrl
    final List<String> pagesUrl

    FylChapter(String page_id, int pages, String name, String chapter_url, List<String> pages_url) {
        this.pageId = page_id
        this.pages = pages
        this.name = name
        this.chapterUrl = chapter_url
        this.pagesUrl = pages_url
    }
}
