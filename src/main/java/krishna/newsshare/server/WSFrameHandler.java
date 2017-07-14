package krishna.newsshare.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WSFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private WebSocketServerHandshaker handshaker;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		handleWebSocketFrame(ctx, msg);
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) {

		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}

		// Send the uppercase string back.
		String request = ((TextWebSocketFrame) frame).text();
		System.err.printf("%s received %s%n", ctx.channel(), request);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		//If it is a websocket, handle it
		if(evt instanceof WebSocketServerHandshaker) {
			this.handshaker = (WebSocketServerHandshaker) evt;
		} else {
			//Else pass it further pipeline
			ctx.fireUserEventTriggered(evt);
		}
	}
	
	

}
