package me.duncte123.fandomApi;

import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;

public class TempMainClass {
    public static void main(String... args) {
        FandomApi fandomApi = new FandomApi("http://dannyphantom.wikia.com");
        LocalWikiSearchResultSet wikiSearchResultSet =  fandomApi.searchEndpoints.list("danny");
        System.out.println(wikiSearchResultSet.getItems().get(0).getSnippet()
                .replaceAll("<span class=\"searchmatch\">", "**")
                .replaceAll("</span>","**")
        );
    }
}
