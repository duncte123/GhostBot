package me.duncte123.fandomApi.models.search;

public class LocalWikiSearchResult {

    private final int quality;
    private final String url;
    private final int ns;
    private final int id;
    private final String title;
    private final String snippet;

    public LocalWikiSearchResult(int quality, String url, int ns, int id, String title, String snippet) {
        this.quality = quality;
        this.url = url;
        this.ns = ns;
        this.id = id;
        this.title = title;
        this.snippet = snippet;
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
