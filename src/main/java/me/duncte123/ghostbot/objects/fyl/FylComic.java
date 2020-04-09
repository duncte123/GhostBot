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

import java.util.List;

@SuppressWarnings("unused")
public class FylComic {
    private String baseUrl;
    private List<FylChapter> chapters;
    private boolean useWixUrl;
    private String wixUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<FylChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<FylChapter> chapters) {
        this.chapters = chapters;
    }

    public boolean isUseWixUrl() {
        return useWixUrl;
    }

    public void setUseWixUrl(boolean useWixUrl) {
        this.useWixUrl = useWixUrl;
    }

    public String getWixUrl() {
        return wixUrl;
    }

    public void setWixUrl(String wixUrl) {
        this.wixUrl = wixUrl;
    }
}
