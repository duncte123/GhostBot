package me.duncte123.fandomApi.endpoints;

import me.duncte123.fandomApi.endpoints.search.List;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;

public class Endpoints {

    public static class Search {
        public LocalWikiSearchResultSet list(String query) {
            return new List(query).execute();
        }
    }
}

