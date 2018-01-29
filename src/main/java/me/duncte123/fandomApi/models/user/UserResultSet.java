package me.duncte123.fandomApi.models.user;

import me.duncte123.fandomApi.models.FandomResult;

import java.util.List;

public class UserResultSet implements FandomResult {

    private final String basePath;
    private final List<UserElement> items;

    public UserResultSet(String basePath, List<UserElement> items) {
        this.basePath = basePath;
        this.items = items;
    }

    public List<UserElement> getItems() {
        return items;
    }

    public String getBasePath() {
        return basePath;
    }
}
