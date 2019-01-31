package revolut.backend.test.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import revolut.backend.test.response.APIResponseCode;
import revolut.backend.test.response.BaseResponse;


/**
 * utility classes are bad for OOP, but useful in some case
 */
public class HttpUtils {


    // prevent accidentally instantiation and inheritance
    private HttpUtils() {
    }

    public static void writeOkResponse(ChannelHandlerContext context) {
        writeResponse(context, HttpResponseStatus.OK, APIResponseCode.OK);
    }

    public static <T> void writeOkResponse(ChannelHandlerContext context, BaseResponse<T> response) {
        writeResponse(context, HttpResponseStatus.OK, Json.encodeAsBytes(response));
    }

    public static void writeNotFoundResponse(ChannelHandlerContext context, APIResponseCode apiResponseCode) {
        writeResponse(context, HttpResponseStatus.NOT_FOUND, apiResponseCode);
    }


    public static void writeInternalServerError(ChannelHandlerContext context) {
        writeResponse(context, HttpResponseStatus.INTERNAL_SERVER_ERROR, APIResponseCode.UNKNOWN_ERROR);
    }

    public static void writeInternalServerError(ChannelHandlerContext context, APIResponseCode apiResponseCode) {
        writeResponse(context, HttpResponseStatus.INTERNAL_SERVER_ERROR, apiResponseCode);
    }

    public static void writeForbidden(ChannelHandlerContext context, APIResponseCode apiResponseCode) {
        writeResponse(context, HttpResponseStatus.FORBIDDEN, apiResponseCode);
    }

    public static void writeBadRequest(ChannelHandlerContext context, APIResponseCode apiResponseCode) {
        writeResponse(context, HttpResponseStatus.BAD_REQUEST, apiResponseCode);
    }

    public static void writeResponse(ChannelHandlerContext context,
                                     HttpResponseStatus httpStatus,
                                     APIResponseCode apiResponseCode) {
        BaseResponse<Void> response = BaseResponse.create(apiResponseCode);
        writeResponse(context, httpStatus, Json.encodeAsBytes(response));
    }

    private static void writeResponse(ChannelHandlerContext context,
                                      HttpResponseStatus status,
                                      byte[] content) {
        ByteBuf buf = Unpooled.copiedBuffer(content);
        writeResponse(context, status, buf, content.length);
    }

    private static void writeResponse(ChannelHandlerContext context,
                                      HttpResponseStatus status,
                                      ByteBuf byteBuf,
                                      int contentLength) {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength))
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        //we don't need keep-alive, so don't forget to close channel
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
