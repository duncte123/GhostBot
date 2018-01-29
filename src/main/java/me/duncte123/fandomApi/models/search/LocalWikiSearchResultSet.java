package me.duncte123.fandomApi.models.search;

import me.duncte123.fandomApi.models.FandomResult;

import java.util.List;

public class LocalWikiSearchResultSet implements FandomResult {
    private final int batches;
    private final List<LocalWikiSearchResult> items;
    private final int total;
    private final String currentBatch;
    private final int next;

    public LocalWikiSearchResultSet(int batches, List<LocalWikiSearchResult> items, int total, String currentBatch, int next) {
        this.batches = batches;
        this.items = items;
        this.total = total;
        this.currentBatch = currentBatch;
        this.next = next;
    }

    public int getBatches() {
        return batches;
    }

    public String getCurrentBatch() {
        return currentBatch;
    }

    public int getNext() {
        return next;
    }

    public int getTotal() {
        return total;
    }

    public int getNextBatch() {
        return Integer.valueOf(currentBatch) + 1;
    }

    public List<LocalWikiSearchResult> getItems() {
        return items;
    }

}
