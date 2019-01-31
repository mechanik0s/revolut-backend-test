package revolut.backend.test.routes;

import io.netty.handler.codec.http.HttpMethod;

public interface Route {
    Handler<RoutingContext> handler();

    boolean matches(String path, HttpMethod method);
}
