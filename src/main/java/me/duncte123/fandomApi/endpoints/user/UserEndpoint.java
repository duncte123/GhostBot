package me.duncte123.fandomApi.endpoints.user;

import me.duncte123.fandomApi.endpoints.Endpoint;

public abstract class UserEndpoint extends Endpoint {
    @Override
    public String getEndpoint() {
        return super.getEndpoint() + "/User/" + getClass().getSimpleName();
    }
}
