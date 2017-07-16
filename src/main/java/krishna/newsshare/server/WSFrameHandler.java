package krishna.newsshare.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import krishna.newsshare.datastructure.Update;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WSFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
	private static Logger log = LoggerFactory.getLogger(WSFrameHandler.class);
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
			log.warn("Frame not a TextWebSocketFrame {}. Ignoring",frame);
			return;
		}

		// Send the uppercase string back.
		String request = ((TextWebSocketFrame) frame).text();
		System.err.printf("%s received %s%n", ctx.channel(), request);
		Update update = convertIncomingToUpdate(request);
		if(update != null) {
			ctx.fireChannelRead(update);
		}
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
	
	protected Update convertIncomingToUpdate(String json) {
		Update update = null;
		try {
			Gson gson = new GsonBuilder().create();
			update = gson.fromJson(json,Update.class);
		} catch(JsonSyntaxException ex) {
			log.error("Error converting {} to valid json",json,ex);
		}
		return validate(update);
	}
	
	private static final int TOPIC_MAX_LENGTH = 255;
	private Update validate(Update update) {
		if(update == null) {
			return null;
		}
		if(update.getName() == null|| update.getName().isEmpty()) {
			return null;
		}
		if(update.getName().length() > TOPIC_MAX_LENGTH) {
			return null;
		}
		if(update.getUpdateType() == null) {
			return null;
		}
		return update;
	}
	

}
