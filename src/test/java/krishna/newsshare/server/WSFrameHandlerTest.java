package krishna.newsshare.server;

import static org.junit.Assert.*;
import krishna.newsshare.datastructure.Update;
import krishna.newsshare.datastructure.Update.UpdateType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WSFrameHandlerTest {
	
	private EmbeddedChannel ec;
	
	@Before
	public void setup() {
		//Prepare channel with WSFrame handler
		WSFrameHandler handler = new WSFrameHandler();
		ec = new EmbeddedChannel(handler);
	}
	@Test
	public void testWSFramePing() {
		//Prepare and fire ping frame
		PingWebSocketFrame ping = new PingWebSocketFrame();
		ec.writeInbound(ping);
		ec.flush();
		
		//Upon receiving ping, it should fire pong message
		Object obj = ec.readOutbound();
		assertTrue(obj instanceof PongWebSocketFrame);
	}
	
	@Test
	public void testWSFrameTextValidJson() {
		//Prepare and fire valid update
		Update update = new Update("Topic",UpdateType.DOWNVOTE);
		TextWebSocketFrame textFrame = new TextWebSocketFrame(getJsonString(update));
		ec.writeInbound(textFrame);
		ec.flush();
		//Upon receiving valid json, it should fire update message
		//to dowmstream handlers
		Object obj = ec.readInbound();
		assertTrue(obj instanceof Update);
		
		Update expected = (Update) obj;
		assertTrue(expected.getName().equals(update.getName()));
		assertTrue(expected.getUpdateType().equals(update.getUpdateType()));
	}
	
	@Test
	public void testWSFrameTextInValidJson() {
		//Prepare and fire valid update
		String invalid = "{\"hahaha\" : \"haha\"}";
		TextWebSocketFrame textFrame = new TextWebSocketFrame(invalid);
		ec.writeInbound(textFrame);
		ec.flush();
		
		//Message will be ignored
		//Nothing should be fired
		assertTrue(ec.readInbound() == null);
		assertTrue(ec.readOutbound() == null);
	}
	
	@Test
	public void testWSFrameUnSupported() {
		//Except text,ping,and close,otherframes should be ignored
		BinaryWebSocketFrame bFame = new BinaryWebSocketFrame();
		ec.writeInbound(bFame);
		//Nothing should be fired
		assertTrue(ec.readInbound() == null);
		assertTrue(ec.readOutbound() == null);
	}
	
	@Test
	public void testWSFrameClose() {
		CloseWebSocketFrame closeFrame = new CloseWebSocketFrame();
		WebSocketServerHandshaker shaker = Mockito.mock(WebSocketServerHandshaker.class);
		SimpleChannelInboundHandler<CloseWebSocketFrame> handler = prepareDummyHandshakeTriggerer(shaker);
		//Add this handler as first handler in pipeline
		ec.pipeline().addFirst(handler);
		ec.writeInbound(closeFrame);
		
		//Verify shaker is closed
		Mockito.verify(shaker).close(Mockito.any(),Mockito.any());
	}
	
	private SimpleChannelInboundHandler<CloseWebSocketFrame> prepareDummyHandshakeTriggerer(WebSocketServerHandshaker shaker) {
		return new SimpleChannelInboundHandler<CloseWebSocketFrame>() {

			@Override
			protected void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame msg)
					throws Exception {
				//Trigger event
				ctx.fireUserEventTriggered(shaker);
				//Trigger message
				ctx.fireChannelRead(msg);
			}
		};
		
	}
	
	private String getJsonString(Update update) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(update);
	}

}
