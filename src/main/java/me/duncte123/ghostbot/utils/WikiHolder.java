/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.utils;

public class WikiHolder {

    private final String domain;
    private final String searchListEndpoint;
    private final String userDetailsEndpoint;

    public WikiHolder(String domain) {
        this.domain = domain;
        final String apiBase = this.domain + "/api/v1";
        this.searchListEndpoint = apiBase + "/Search/List";
        this.userDetailsEndpoint = apiBase + "/User/Details";
    }

    public String getDomain() {
        return domain;
    }

    public String getSearchListEndpoint() {
        return searchListEndpoint;
    }

    public String getUserDetailsEndpoint() {
        return userDetailsEndpoint;
    }
}
