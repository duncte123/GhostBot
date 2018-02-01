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

package me.duncte123.fandomApi.endpoints.user;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.models.user.UserElement;
import me.duncte123.fandomApi.models.user.UserResultSet;
import me.duncte123.ghostBot.utils.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Details extends UserEndpoint {

    private final String ids;
    private final int size;

    public Details(String ids) {
        this(ids, 100);
    }

    public Details(String ids, int size) {
        this.ids = ids;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getIds() {
        return ids;
    }

    @Override
    public FandomResult execute() {

        try {
            Ason ason = WebUtils.getAson(getEndpoint() + String.format("?ids=%s&size=%s",
                    ids,
                    size
            ));

            if (ason.has("exception")) {
                return new FandomException(
                        ason.getString("exception.type"),
                        ason.getString("exception.message"),
                        ason.getInt("exception.code"),
                        ason.getString("exception.details"),
                        ason.getString("trace_id")
                );
            }

            String basePath = ason.getString("basepath");
            AsonArray<Ason> items = ason.getJsonArray("items");
            List<UserElement> users = new ArrayList<>();
            for (Ason item : items) {
                users.add(new UserElement(
                        item.getString("name"),
                        item.getString("avatar"),
                        item.getString("url"),
                        item.getInt("user_id"),
                        item.getInt("numberofedits"),
                        item.getString("title"),
                        basePath
                ));
            }

            return new UserResultSet(basePath, users);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
