package krishna.newsshare.server;

import krishna.newsshare.datastructure.TopicRepo;
import krishna.newsshare.datastructure.Update;
import krishna.newsshare.datastructure.Update.UpdateType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class UpdateCommitter extends SimpleChannelInboundHandler<Update> {
	private TopicRepo topicRepo;
	
	public UpdateCommitter(TopicRepo tr) {
		this.topicRepo = tr;
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Update msg)
			throws Exception {
		if(msg.getUpdateType() == UpdateType.TOPIC) {
			topicRepo.addTopic(msg.getTopic());
		} else if(msg.getUpdateType() == UpdateType.UPVOTE) {
			topicRepo.upvoteTopic(msg.getTopic());
		} else if(msg.getUpdateType() == UpdateType.DOWNVOTE) {
			topicRepo.downvoteTopic(msg.getTopic());
		}
		
	}

}
