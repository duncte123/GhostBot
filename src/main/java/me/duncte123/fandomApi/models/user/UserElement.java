package me.duncte123.fandomApi.models.user;

public class UserElement {

    private final String name;
    private final String avatar;
    private final String relativeUrl;
    private final int userId;
    private final int numberofedits;
    private final String title;

    private final String basePath;

    public UserElement(String name, String avatar, String relativeUrl, int userId, int numberofedits, String title, String basePath) {
        this.name = name;
        this.avatar = avatar;
        this.relativeUrl = relativeUrl;
        this.userId = userId;
        this.numberofedits = numberofedits;
        this.title = title;
        this.basePath = basePath;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public String getAbsoluteUrl() {
        return basePath + relativeUrl;
    }

    public int getUserId() {
        return userId;
    }

    public int getNumberofedits() {
        return numberofedits;
    }

    public String getTitle() {
        return title;
    }
}
