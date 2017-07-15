package krishna.newsshare.server;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import krishna.newsshare.datastructure.TopicRepo;
import krishna.newsshare.datastructure.Update;
import krishna.newsshare.datastructure.Update.UpdateType;
import krishna.newsshare.datastructure.VotedTopic;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

@Sharable
public class UpdateCommitter extends SimpleChannelInboundHandler<Update> {
	private static final int NO_OF_TOPICS = 20;
	private TopicRepo topicRepo;
	private ChannelGroup sockets;
	
	public UpdateCommitter(TopicRepo tr) {
		this.topicRepo = tr;
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
		if(evt instanceof RequestUpgrader.NewWSEvent) {
			//Add this socket to channel
			sockets.add(ctx.channel());
		} else {
			ctx.fireUserEventTriggered(evt);
		}
		
	}
	
	private void sendTopTopics() {
		List<VotedTopic> list = topicRepo.getTopTopics(NO_OF_TOPICS);
		String json = convertToJson(list);
		System.out.println("Sending " + json);
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
