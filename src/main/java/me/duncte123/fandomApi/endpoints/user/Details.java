package me.duncte123.fandomApi.endpoints.user;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.models.user.UserElement;
import me.duncte123.fandomApi.models.user.UserResultSet;
import me.duncte123.ghostBot.utils.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Details extends UserEndpoint {

    private final String ids;
    private final int size;

    public Details(String ids) {
        this(ids, 100);
    }

    public Details(String ids, int size) {
        this.ids = ids;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getIds() {
        return ids;
    }

    @Override
    public FandomResult execute() {

        try {
            Ason ason = WebUtils.getAson(getEndpoint() + String.format("?ids=%s&size=%s",
                    ids,
                    size
            ));

            if (ason.has("exception")) {
                return new FandomException(
                        ason.getString("exception.type"),
                        ason.getString("exception.message"),
                        ason.getInt("exception.code"),
                        ason.getString("exception.details"),
                        ason.getString("trace_id")
                );
            }

            String basePath = ason.getString("basepath");
            AsonArray<Ason> items = ason.getJsonArray("items");
            List<UserElement> users = new ArrayList<>();
            for (Ason item : items) {
                users.add(new UserElement(
                        item.getString("name"),
                        item.getString("avatar"),
                        item.getString("url"),
                        item.getInt("user_id"),
                        item.getInt("numberofedits"),
                        item.getString("title"),
                        basePath
                ));
            }

            return new UserResultSet(basePath, users);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
