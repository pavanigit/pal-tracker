package io.pivotal.pal.tracker;

import org.springframework.web.client.RestOperations;

public class ProjectClient {
    String userServiceUrl;
    RestOperations restOp;

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public RestOperations getRestOp() {
        return restOp;
    }

    public void setRestOp(RestOperations restOp) {
        this.restOp = restOp;
    }

    public ProjectClient(String userServiceUrl, RestOperations restOp) {
        this.userServiceUrl = userServiceUrl;
        this.restOp = restOp;
    }
}
