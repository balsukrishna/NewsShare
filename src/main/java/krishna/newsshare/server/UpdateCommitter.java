package krishna.newsshare.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import krishna.newsshare.datastructure.TopicRepo;
import krishna.newsshare.datastructure.Update;
import krishna.newsshare.datastructure.Update.UpdateType;
import krishna.newsshare.datastructure.VotedTopic;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * A Single UpdateCommitter is added to all the channel's pipeline.
 * This should be thread-safe or guranteed to be accessed by one thread
 * @author krishna
 *
 */
@Sharable
public class UpdateCommitter extends SimpleChannelInboundHandler<Update> {
	private static Logger log  = LoggerFactory.getLogger(UpdateCommitter.class);
	private static final int NO_OF_TOPICS = 20;
	private TopicRepo topicRepo;
	
	/**
	 * Channelgroup encapsualtes all existing WS connections.
	 * It will auto remove disconnected channels from channelgroup
	 */
	private ChannelGroup sockets;
	
	public UpdateCommitter(TopicRepo tr) {
		this.topicRepo = tr;
		//This executor is used to notify futures of actions on channelgroup.
		//Nobody is listening,so it's ok to use this default executor
		this.sockets = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Update msg)
			throws Exception {
		if(msg.getUpdateType() == UpdateType.TOPIC) {
			topicRepo.addTopic(msg.getName());
		} else if(msg.getUpdateType() == UpdateType.UPVOTE) {
			topicRepo.upvoteTopic(msg.getName());
		} else if(msg.getUpdateType() == UpdateType.DOWNVOTE) {
			topicRepo.downvoteTopic(msg.getName());
		}
		sendTopTopics();
	}


	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if(evt instanceof HttpRequestHandler.NewWSEvent) {
			//Add this socket to channelgroup
			log.info("Receied new WS Connection {}.Sending top topics",ctx.channel());
			sockets.add(ctx.channel());
			sendFirstMessage(ctx.channel());
		} else {
			ctx.fireUserEventTriggered(evt);
		}
		
	}
	
	private void sendFirstMessage(Channel ch) {
		List<VotedTopic> list = topicRepo.getTopTopics(NO_OF_TOPICS);
		String json = convertToJson(list);
		TextWebSocketFrame frame = new TextWebSocketFrame(json);
		ch.writeAndFlush(frame);
	}
	
	private void sendTopTopics() {
		List<VotedTopic> list = topicRepo.getTopTopics(NO_OF_TOPICS);
		String json = convertToJson(list);
		log.trace("Sending " + json);
		TextWebSocketFrame frame = new TextWebSocketFrame(json);
		sockets.writeAndFlush(frame);
	}
	
	private String convertToJson(List<VotedTopic> votedTopicList) {
		JsonObject obj = new JsonObject();
		for(VotedTopic topic : votedTopicList) {
			JsonArray votes = new JsonArray();
			votes.add(topic.getUpvotes());
			votes.add(topic.getDownvotes());
			obj.add(topic.getTopic(), votes);
		}
		return obj.toString();
	}

	
}
