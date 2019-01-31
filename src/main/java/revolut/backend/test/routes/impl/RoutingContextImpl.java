package revolut.backend.test.routes.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import revolut.backend.test.routes.RequestData;
import revolut.backend.test.routes.RoutingContext;

public class RoutingContextImpl implements RoutingContext {
    private final ChannelHandlerContext channelHandlerContext;
    private final RequestData requestData;
    private final String path;
    private final String rawURI;
    private final HttpMethod httpMethod;

    public RoutingContextImpl(ChannelHandlerContext context, FullHttpRequest httpRequest) {
        this.channelHandlerContext = context;
        this.httpMethod = httpRequest.method();
        QueryStringDecoder uriDecoder = new QueryStringDecoder(httpRequest.uri(), CharsetUtil.UTF_8);
        this.path = uriDecoder.path();
        this.rawURI = uriDecoder.uri();
        // some magic, cause netty use direct memory allocation for own buffers
        byte[] bytes;
        ByteBuf byteBuf = httpRequest.content();
        if (byteBuf.hasArray()) {
            bytes = byteBuf.array();
        } else {
            bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        }
        this.requestData = new RequestDataImpl(uriDecoder.parameters(), bytes);
    }

    @Override
    public ChannelHandlerContext channelContext() {
        return channelHandlerContext;
    }

    @Override
    public HttpMethod method() {
        return httpMethod;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String toString() {
        return "RoutingContextImpl{" +
                "URI='" + rawURI + '\'' +
                '}';
    }

    @Override
    public RequestData requestData() {
        return requestData;
    }
}
