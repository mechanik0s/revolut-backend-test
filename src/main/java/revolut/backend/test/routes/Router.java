package revolut.backend.test.routes;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Optional;

public interface Router {
    Router addRoute(String path, HttpMethod httpMethod, Handler<RoutingContext> handler);

    default Router get(String path, Handler<RoutingContext> handler) {
        return addRoute(path, HttpMethod.GET, handler);
    }

    default Router post(String path, Handler<RoutingContext> handler) {
        return addRoute(path, HttpMethod.POST, handler);
    }

    default Router delete(String path, Handler<RoutingContext> handler) {
        return addRoute(path, HttpMethod.DELETE, handler);
    }

    default Router put(String path, Handler<RoutingContext> handler) {
        return addRoute(path, HttpMethod.PUT, handler);
    }

    Optional<Route> findRoute(String path, HttpMethod method);
}
