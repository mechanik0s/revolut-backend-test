package revolut.backend.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.backend.test.exceptions.NoSuchRouteException;
import revolut.backend.test.response.APIResponseCode;
import revolut.backend.test.routes.Route;
import revolut.backend.test.routes.Router;
import revolut.backend.test.routes.RoutingContext;
import revolut.backend.test.routes.impl.RoutingContextImpl;
import revolut.backend.test.utils.HttpUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.channel.ChannelHandler.Sharable;

public class NettyServer {

    private final Logger logger;
    private final Router router;
    private final ExecutorService executorService;
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(int port, Router router) {
        this.router = router;
        this.executorService = Executors.newCachedThreadPool();
        this.port = port;
        this.logger = LoggerFactory.getLogger("Netty-Server");
    }

    public void start() {
        Class<? extends ServerChannel> serverSocketChannelClass;
        // use native speed-hack if you can
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup();
            serverSocketChannelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            serverSocketChannelClass = NioServerSocketChannel.class;
        }
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(serverSocketChannelClass)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpChannelInitializer());
            ChannelFuture startFuture = b.bind(port).sync();
            logger.info("HTTP SERVER STARTED ON {}", startFuture.channel().localAddress());
        } catch (InterruptedException e) {
            logger.error("SERVER CANNOT STARTED", e);
        }
    }

    public void shutdown() {
        try {
            bossGroup.shutdownGracefully().await();
            workerGroup.shutdownGracefully().await();
        } catch (InterruptedException e) {
            logger.error("SERVER CANNOT SHUTDOWN", e);
        }
        logger.info("SERVER GRACEFULLY SHUTDOWN");
    }

    class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline()
                    .addLast("codec", new HttpServerCodec())
                    .addLast("aggregator", new HttpObjectAggregator(1048576)) //1MB enough
                    .addLast("logger", new LoggingHandler(LogLevel.INFO))
                    .addLast("handler", new RouterHandler());
        }
    }

    @Sharable // reduce unnecessary handlers objects if they have not shared
    public class RouterHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Let's handle all non-logic troubles here
            try {
                if (cause instanceof NoSuchRouteException) {
                    logger.warn("No route found for this path!");
                    HttpUtils.writeNotFoundResponse(ctx, APIResponseCode.NO_ROUTE);
                } else {
                    logger.error("Houston, we have a problem", cause);
                    HttpUtils.writeInternalServerError(ctx);
                }
            } finally {
                if (ctx.channel().isActive()) {
                    ctx.close();
                }
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof FullHttpRequest)) {
                return;
            }
            final FullHttpRequest request = (FullHttpRequest) msg;
            RoutingContext routingContext = new RoutingContextImpl(ctx, request);
            Route route = router.findRoute(routingContext.path(), routingContext.method())
                    .orElseThrow(NoSuchRouteException::new);
            // we don't want to block the event loop, huh?
            executorService.submit(() -> {
                final Thread currentThread = Thread.currentThread();
                final String oldName = currentThread.getName();
                currentThread.setName(oldName + "-processing-" + routingContext);
                try {
                    route.handler().handle(routingContext);
                } catch (Exception e) {
                    logger.warn("Houston, we have a problem", e);
                    HttpUtils.writeInternalServerError(ctx);
                } finally {
                    // remember about socket leak!
                    if (ctx.channel().isActive()) {
                        ctx.close();
                    }
                    currentThread.setName(oldName);
                }
            });
        }
    }
}


