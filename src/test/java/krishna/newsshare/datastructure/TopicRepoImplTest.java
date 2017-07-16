package krishna.newsshare.datastructure;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TopicRepoImplTest {

	
	@Test
	public void testGetTopTopics() {
		TopicRepoImpl tr = new TopicRepoImpl(1);
		
		//Add a topic
		tr.addTopic("topic1");
		tr.upvoteTopic("topic1");
		
		//Add another topic
		tr.addTopic("topic2");
		tr.addTopic("topic3");
		
		//Should return 1 topic only
		assertTrue(tr.getTopTopics().size() == 1);
		//Top topic should be topic1
		assertTrue(tr.getTopTopics().get(0).getTopic().equals("topic1"));
	}

	@Test
	public void testAddTopic() {
		TopicRepoImpl tr = new TopicRepoImpl(2);
		tr.addTopic("topic1");
		assertTrue(tr.getTopTopics().size() == 1);
		
		//Readding existing topic shouldnt add any new topics
		tr.addTopic("topic1");
		assertTrue(tr.getTopTopics().size() == 1);
		
		//Default votes is 0
		assertTrue(tr.getTopTopics().get(0).getUpvotes() == 0);
		assertTrue(tr.getTopTopics().get(0).getDownvotes() == 0);
		assertTrue(tr.getTopTopics().get(0).getTopic().equals("topic1"));
	}	

	@Test
	public void testUpvoteTopic() {
		TopicRepoImpl tr = new TopicRepoImpl(1);
		tr.addTopic("topic1");
		tr.upvoteTopic("topic1");
		assertTrue(tr.getTopTopics().get(0).getUpvotes() == 1);
	}
	
	@Test
	public void testUpvoteTopicNonExistentTopic() {
		TopicRepoImpl tr = new TopicRepoImpl(1);
		tr.upvoteTopic("topic1");
		//Since no topic present, vote should be ignored
		assertTrue(tr.getTopTopics().size() == 0);
	}


	@Test
	public void testDownvoteTopic() {
		TopicRepoImpl tr = new TopicRepoImpl(1);
		tr.addTopic("topic1");
		tr.downvoteTopic("topic1");
		tr.downvoteTopic("topic1");
		assertTrue(tr.getTopTopics().get(0).getDownvotes() == 2);
	}
	
	@Test
	public void testDownvoteTopicNonExistentTopic() {
		TopicRepoImpl tr = new TopicRepoImpl(1);
		tr.downvoteTopic("topic1");
		//Since no topic present, vote should be ignored
		assertTrue(tr.getTopTopics().size() == 0);
	}
	
	@Test
	public void tesOrdering() {
		TopicRepoImpl tr = new TopicRepoImpl(5);
		tr.addTopic("1");
		tr.addTopic("2");
		tr.addTopic("3");
		tr.addTopic("4");
		
		List<VotedTopic> topics = tr.getTopTopics();
		assertTrue(topics.size() == 4);
		
		tr.addTopic("5");
		
		tr.addTopic("6");
		//vote 6
		tr.upvoteTopic("6");
		
		topics = tr.getTopTopics();
		VotedTopic sixth = null;
		for(VotedTopic vt : topics) {
			if(vt.getTopic().equals("6")) {
				sixth = vt;
			}
		}
		assertNotNull(sixth);
	}

}
