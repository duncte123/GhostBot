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

package me.duncte123.fandomApi.endpoints.search;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;
import me.duncte123.ghostBot.utils.WebUtils;

import java.io.IOException;
import java.util.ArrayList;

public class List extends SearchEndpoint {
    private final String query;
    private final String type;
    private final String rank;
    private final int limit;
    private final int minArticleQuality;
    private final int batch;
    private final String namespaces;

    public List(String query) {
        this(query, "articles", "newest", 10, 10, 1, "0,14");
    }

    public List(String query, int batch) {
        this(query, "articles", "newest", 10, 10, batch, "0,14");
    }

    public List(String query, String type, String rank) {
        this(query, type, rank, 25, 10, 1, "0,14");
    }

    public List(String query, String type, String rank, int limit, int minArticleQuality, int batch, String namespaces) {
        this.query = query;
        this.type = type;
        this.rank = rank;
        this.limit = limit;
        this.minArticleQuality = minArticleQuality;
        this.batch = batch;
        this.namespaces = namespaces;
    }

    public int getBatch() {
        return batch;
    }

    public int getLimit() {
        return limit;
    }

    public int getMinArticleQuality() {
        return minArticleQuality;
    }

    public String getNamespaces() {
        return namespaces;
    }

    public String getQuery() {
        return query;
    }

    public String getRank() {
        return rank;
    }

    public String getType() {
        return type;
    }

    @Override
    public FandomResult execute() {
        try {
            Ason ason = WebUtils.getAson(getEndpoint() + String.format(
                    "?query=%s&type=%s&rank=%s&limit=%s&minArticleQuality=%s&batch=%s&namespaces=%s",
                    query.replaceAll(" ", "+"),
                    type,
                    rank,
                    limit,
                    minArticleQuality,
                    batch,
                    namespaces
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
            java.util.List<LocalWikiSearchResult> results = new ArrayList<>();
            AsonArray<Ason> items = ason.getJsonArray("items");
            for (Ason item : items) {
                results.add(new LocalWikiSearchResult(
                        item.getInt("quality"),
                        item.getString("url"),
                        item.getInt("ns"),
                        item.getInt("id"),
                        item.getString("title"),
                        item.getString("snippet")
                ));
            }
            return new LocalWikiSearchResultSet(
                    ason.getInt("batches"),
                    results,
                    ason.getInt("total"),
                    ason.getString("currentBatch"),
                    ason.getInt("next")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
