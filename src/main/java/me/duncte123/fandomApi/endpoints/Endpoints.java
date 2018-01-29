package me.duncte123.fandomApi.endpoints;

import me.duncte123.fandomApi.endpoints.search.List;
import me.duncte123.fandomApi.endpoints.user.Details;
import me.duncte123.fandomApi.models.FandomResult;

public class Endpoints {

    public static class Search {
        public FandomResult list(String query) {
            return new List(query).execute();
        }

        public FandomResult list(String query, int batch) {
            return new List(query, batch).execute();
        }
    }

    public static class User {
        public FandomResult details(String ids) {
            return new Details(ids).execute();
        }

        public FandomResult details(String ids, int size) {
            return new Details(ids, size).execute();
        }
    }
}

