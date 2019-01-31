package revolut.backend.test.routes.impl;

import io.netty.handler.codec.http.HttpMethod;
import revolut.backend.test.routes.Handler;
import revolut.backend.test.routes.Route;
import revolut.backend.test.routes.Router;
import revolut.backend.test.routes.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RouterImpl implements Router {
    private List<Route> routes = new ArrayList<>();

    @Override
    public Router addRoute(String path, HttpMethod method, Handler<RoutingContext> handler) {
        routes.add(new RouteImpl(path, method, handler));
        return this;
    }


    @Override
    public Optional<Route> findRoute(String path, HttpMethod method) {
        //You may want to see something like that: routes.stream().filter(route -> route.matches(path, method)).findFirst();
        //But for statement is a bit faster, unless there's so many routes, that using parallelStream is actually makes it faster.
        for (Route route : routes) {
            if (route.matches(path, method)) {
                return Optional.of(route);
            }
        }
        return Optional.empty();
    }
}
