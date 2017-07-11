package krishna.newsshare.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Generates the demo HTML page which is served at http://localhost:8080/
 */
public final class WebSocketServerIndexPage {

    private static final String NEWLINE = "\r\n";

    public static ByteBuf getContent(String webSocketLocation) {
        return Unpooled.copiedBuffer(
                "<html><head><title>Web Socket Test</title></head>" + NEWLINE +
                "<body>" + NEWLINE +
                "<script type=\"text/javascript\">" + NEWLINE +
                "var socket;" + NEWLINE +
                "socket = new WebSocket(\"" + webSocketLocation + "\");" + NEWLINE +
                "socket.onmessage = function(event) {" + NEWLINE +
                "	console.log(event.data);" + NEWLINE +
                "};" + NEWLINE +
                "</script>" + NEWLINE +
                "</body>" + NEWLINE +
                "</html>" + NEWLINE, CharsetUtil.US_ASCII);
    }

}