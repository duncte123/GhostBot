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

package me.duncte123.ghostbot.objects.fyl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
public class FylChapter {

    private String page_id;
    private int pages;
    private String name;
    private String chapter_url;
    private List<String> pages_url;

    @JsonProperty("page_id")
    public void setPageId(String page_id) {
        this.page_id = page_id;
    }

    @JsonProperty("chapter_url")
    public void setChapterUrl(String chapter_url) {
        this.chapter_url = chapter_url;
    }

    @JsonProperty("pages_url")
    public void setPagesUrl(List<String> pages_url) {
        this.pages_url = pages_url;
    }

    public String getPageId() {
        return page_id;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapterUrl() {
        return chapter_url;
    }

    public List<String> getPagesUrl() {
        return pages_url;
    }
}
