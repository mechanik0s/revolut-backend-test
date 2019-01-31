package revolut.backend.test.routes.impl;

import io.netty.handler.codec.http.HttpMethod;
import revolut.backend.test.routes.Handler;
import revolut.backend.test.routes.Route;
import revolut.backend.test.routes.RoutingContext;

public class RouteImpl implements Route {
    private final String path;
    private final HttpMethod method;
    private final Handler<RoutingContext> handler;

    public RouteImpl(String path, HttpMethod method, Handler<RoutingContext> handler) {
        this.path = path;
        this.method = method;
        this.handler = handler;
    }

    @Override
    public Handler<RoutingContext> handler() {
        return handler;
    }

    public boolean matches(String path, HttpMethod method) {
        return this.method.equals(method) && this.path.equals(path);
    }
}
