package krishna.newsshare.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * A new Request Handler is added to every channel's pipeline <br>
 * <b>Responsibility:</b>: <br>
 *  1. For HomePage uri,Return a html containing index <br> 
 *  2. For voteFeed uri(expected to have upgrade header),upgrage to WS Connection,
 *  	Trigger an handShake event <br>
 *  3. For Other uri, return empty 200 response
 * @author krishna
 *
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);
	
	private static final String WS_PATH = "/voteFeed";

    private String index;
    public HttpRequestHandler(String indexContent) {
    	this.index = indexContent;
	}

	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
    	//If home page, Send  index
        if ("/".equals(req.uri())) {
            ByteBuf content = Unpooled.copiedBuffer(index.getBytes());
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        //If votefeed upgrade
        } else if(WS_PATH.equals(req.uri())) {
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = 
            		new WebSocketServerHandshakerFactory(req.uri(), null, true);
            WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ChannelFuture complete = handshaker.handshake(ctx.channel(), req);
                addListenerToTriggerNewWSEvent(complete);
                //attachPeriodicPinger(complete);
            }
            ctx.fireUserEventTriggered(handshaker);
        } else {
        	//Send Default response
        	ByteBuf content = Unpooled.copiedBuffer("".getBytes());
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;

        }


    }
    
    /**
     * Event which indicates connection upgrade to 
     * WS is complete
     * @author krishna
     *
     */
    public static class NewWSEvent {}

	/**
	 * Add a listener, which , if future is success, trigger 
	 * {@link #NewWSEvent} event
	 * @param future
	 */
	private void addListenerToTriggerNewWSEvent(ChannelFuture future) {
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				//Trigger a new WS Event
				if (future.isSuccess()) {
					future.channel().pipeline().fireUserEventTriggered(new NewWSEvent());
				}
			}
		});
	}
    
    /**
     * Add a listener, which , if future is success,
     * sends epoch as text frame periodically to connection 
     * @param future
     */
    private void attachPeriodicPinger(ChannelFuture future) {
    	future.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					Channel ch = future.channel();
					log.info("Creating a scheduled task to ping connection {} ",ch);
					ch.eventLoop().scheduleWithFixedDelay(() -> {
						ch.writeAndFlush(new TextWebSocketFrame("Server time is " +	System.currentTimeMillis()));
					}, 1000, 1000, TimeUnit.MILLISECONDS);
				}
			}
		});
    }
    

   
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
    	
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
           HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.error("Error. Closing channel",cause);
        ctx.close();
    }
}