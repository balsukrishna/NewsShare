package krishna.newsshare.server;

import krishna.newsshare.datastructure.TopicRepo;
import krishna.newsshare.datastructure.Update;
import krishna.newsshare.datastructure.Update.UpdateType;
import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *  EmbeddedChannel can be used for constructing pipeline
 *	without needing actual network connection <br>
 *
 *	https://stackoverflow.com/a/34790002
 * @author krishna
 *
 */
public class UpdateCommitterTest {
		TopicRepo repo;
		EmbeddedChannel ec;
		
	@Before
	public void setup() {
		repo = Mockito.mock(TopicRepo.class);
		//Prepare pipeline with update committer
		UpdateCommitter uc = new UpdateCommitter(repo);
		ec = new EmbeddedChannel(uc);
	}
	
	@Test
	public void testChannelReadNewTopic() {
		//Fire new topic message
		Update newTopic = new Update("topic", UpdateType.TOPIC);
		ec.writeInbound(newTopic);
		
		//Make sure,topic repo is updated
		Mockito.verify(repo,Mockito.times(1)).addTopic(newTopic.getName());
		//Make sure, it gets top topics and send message
		Mockito.verify(repo,Mockito.times(1)).getTopTopics(Mockito.anyInt());
		//Make sure no other method is called
		Mockito.verifyNoMoreInteractions(repo);
	}
	
	@Test
	public void testChannelReadUpvoteTopic() {	
		//Fire Upvote message
		Update newTopic = new Update("topic", UpdateType.UPVOTE);
		ec.writeInbound(newTopic);
		
		//Make sure,topic repo is updated
		Mockito.verify(repo,Mockito.times(1)).upvoteTopic(newTopic.getName());
		//Make sure, it gets top topics and send message
		Mockito.verify(repo,Mockito.times(1)).getTopTopics(Mockito.anyInt());
		//Make sure no other method is called
		Mockito.verifyNoMoreInteractions(repo);
	}
	
	@Test
	public void testChannelReadDownvoteTopic() {	
		//Fire Upvote message
		Update newTopic = new Update("topic", UpdateType.DOWNVOTE);
		ec.writeInbound(newTopic);
		
		//Make sure,topic repo is updated
		Mockito.verify(repo,Mockito.times(1)).downvoteTopic(newTopic.getName());
		//Make sure, it gets top topics and send message
		Mockito.verify(repo,Mockito.times(1)).getTopTopics(Mockito.anyInt());
		//Make sure no other method is called
		Mockito.verifyNoMoreInteractions(repo);
	}

}
