package me.duncte123.fandomApi.endpoints.search;

import me.duncte123.fandomApi.endpoints.Endpoint;

public abstract class SearchEndpoint extends Endpoint {

    @Override
    public String getEndpoint() {
        return super.getEndpoint() + "/Search/" + getClass().getSimpleName();
    }
}
