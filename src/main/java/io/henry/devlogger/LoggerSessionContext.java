package io.henry.devlogger;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by mac on 2016/11/15.
 */
public class LoggerSessionContext {

    private final RoutingContext routeContext;

    private final JsonObject requestData;

    private final LoggerAppContext appContext;

    public LoggerSessionContext(LoggerAppContext appContext, RoutingContext routingContext, JsonObject bizData) {
        this.appContext = appContext;
        this.routeContext = routingContext;
        this.requestData = bizData;
    }

    public LoggerAppContext getAppContext() {
        return appContext;
    }

    public RoutingContext getRouteContext() {
        return routeContext;
    }

    public JsonObject getRequestData() {
        return requestData;
    }
}
