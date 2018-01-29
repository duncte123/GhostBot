package me.duncte123.fandomApi.endpoints.search;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
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

    public LocalWikiSearchResultSet execute() {
        try {
            Ason ason = WebUtils.getAson(getEndpoint() +  String.format(
                    "?query=%s&type=%s&rank=%s&limit=%s&minArticleQuality=%s&batch=%s&namespaces=%s",
                    query,
                    type,
                    rank,
                    limit,
                    minArticleQuality,
                    batch,
                    namespaces
            ));
            if(ason.has("exception")) {
                return null;
            }
            java.util.List<LocalWikiSearchResult> results = new ArrayList<>();
            AsonArray<Ason> items = ason.getJsonArray("items");
            for(Ason item : items) {
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

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
