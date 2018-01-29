package me.duncte123.fandomApi;

import me.duncte123.fandomApi.endpoints.Endpoints;

public class FandomApi {

    private static String wikiUrl;
    public final Endpoints.Search searchEndpoints = new Endpoints.Search();
    public final Endpoints.User userEndpoints = new Endpoints.User();


    public FandomApi(String url) {
        wikiUrl = url;
    }

    public static String getWikiUrl() {
        return wikiUrl;
    }
}
