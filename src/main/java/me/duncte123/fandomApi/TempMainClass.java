package me.duncte123.fandomApi;

import me.duncte123.fandomApi.models.user.UserResultSet;

public class TempMainClass {
    public static void main(String... args) {
        FandomApi fandomApi = new FandomApi("http://dannyphantom.wikia.com");
        UserResultSet userResultSet = (UserResultSet) fandomApi.userEndpoints.details("duncte123");
        System.out.println(userResultSet.getItems().get(0).getAbsoluteUrl());
    }
}
