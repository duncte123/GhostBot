package me.duncte123.fandomApi.endpoints;

import me.duncte123.fandomApi.FandomApi;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.utils.CommonVars;

public abstract class Endpoint {

    public String getEndpoint() {
        return FandomApi.getWikiUrl() + CommonVars.apiBase;
    }

    public abstract FandomResult execute();

}
