package revolut.backend.test.routes;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;

public interface RoutingContext {
    ChannelHandlerContext channelContext();

    HttpMethod method();

    String path();

    RequestData requestData();
}
