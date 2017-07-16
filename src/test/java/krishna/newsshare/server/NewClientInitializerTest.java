package krishna.newsshare.server;

import static org.junit.Assert.*;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class NewClientInitializerTest {
		private SocketChannel channel;
		ChannelPipeline pipeline;
		private ArgumentCaptor<ChannelHandler> handlerCapture;
	
	@Before
	public void setup() {
		pipeline = mockPipeline();
		channel = mockChannel(pipeline);
		handlerCapture = ArgumentCaptor.forClass(ChannelHandler.class);
	}
	
	@Test
	public void testInitChannelSocketChannel() throws Exception {
		NewClientInitializer cl = new NewClientInitializer();
		
		//Call initilize with channel
		cl.initChannel(channel);
		
		//Make sure all handlers are added to pipeline on correct order 
		//codec,aggregator,upgrader,wsframehandler and updatecommitter in same order
		Mockito.verify(pipeline,Mockito.times(4)).addLast(handlerCapture.capture());
		List<ChannelHandler> handlers = handlerCapture.getAllValues();
		assertTrue(handlers.get(0) instanceof HttpServerCodec);
		assertTrue(handlers.get(1) instanceof HttpObjectAggregator);
		assertTrue(handlers.get(2) instanceof HttpRequestHandler);
		assertTrue(handlers.get(3) instanceof WSFrameHandler);
	}
	
	
	@Test
	public void testInitChannelDifferentChannelSameUpdateCommitter() throws Exception {
		NewClientInitializer cl = new NewClientInitializer();
		
		//Prepare second channel
		ChannelPipeline newPipeline = mockPipeline();
		SocketChannel newChannel = mockChannel(newPipeline);
		ArgumentCaptor<ChannelHandler> newChannelCaptor = ArgumentCaptor.forClass(ChannelHandler.class);
		
		//Initialize both channels
		cl.initChannel(channel);
		cl.initChannel(newChannel);
		
		//Get Handlers for both channels
		Mockito.verify(pipeline,Mockito.times(1)).addLast(Mockito.any(DefaultEventExecutorGroup.class),handlerCapture.capture());
		Mockito.verify(pipeline,Mockito.times(1)).addLast(Mockito.any(DefaultEventExecutorGroup.class),newChannelCaptor.capture());
	
		//Make sure update committer is the same handler for both channels
		assertTrue(handlerCapture.getAllValues().get(0) == newChannelCaptor.getAllValues().get(0));
	}

	private SocketChannel mockChannel(ChannelPipeline pipeline) {
		SocketChannel channel = Mockito.mock(SocketChannel.class);
		Mockito.when(channel.pipeline()).thenReturn(pipeline);
		return channel;
	}
	
	private ChannelPipeline mockPipeline() {
		return Mockito.mock(ChannelPipeline.class);
	}
}
