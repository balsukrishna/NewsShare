package krishna.newsshare.datastructure;

import static org.junit.Assert.*;

import org.junit.Test;

public class TopicRepoImplTest {

	
	@Test
	public void testGetTopTopics() {
		TopicRepoImpl tr = new TopicRepoImpl();
		
		//Add a topic
		tr.addTopic("topic1");
		tr.upvoteTopic("topic1");
		
		//Add another topic
		tr.addTopic("topic2");
		tr.addTopic("topic3");
		
		//Should return 1 topic only
		assertTrue(tr.getTopTopics(1).size() == 1);
		//Top topic should be topic1
		assertTrue(tr.getTopTopics(1).get(0).getTopic().equals("topic1"));
	}

	@Test
	public void testAddTopic() {
		TopicRepoImpl tr = new TopicRepoImpl();
		tr.addTopic("topic1");
		assertTrue(tr.getTopTopics(1).size() == 1);
		
		//Readding existing topic shouldnt add any new topics
		tr.addTopic("topic1");
		assertTrue(tr.getTopTopics(2).size() == 1);
		
		//Default votes is 0
		assertTrue(tr.getTopTopics(2).get(0).getUpvotes() == 0);
		assertTrue(tr.getTopTopics(2).get(0).getDownvotes() == 0);
		assertTrue(tr.getTopTopics(2).get(0).getTopic().equals("topic1"));
	}	

	@Test
	public void testUpvoteTopic() {
		TopicRepoImpl tr = new TopicRepoImpl();
		tr.addTopic("topic1");
		tr.upvoteTopic("topic1");
		assertTrue(tr.getTopTopics(1).get(0).getUpvotes() == 1);
	}
	
	@Test
	public void testUpvoteTopicNonExistentTopic() {
		TopicRepoImpl tr = new TopicRepoImpl();
		tr.upvoteTopic("topic1");
		//Since no topic present, vote should be ignored
		assertTrue(tr.getTopTopics(1).size() == 0);
	}


	@Test
	public void testDownvoteTopic() {
		TopicRepoImpl tr = new TopicRepoImpl();
		tr.addTopic("topic1");
		tr.downvoteTopic("topic1");
		tr.downvoteTopic("topic1");
		assertTrue(tr.getTopTopics(1).get(0).getDownvotes() == 2);
	}
	
	@Test
	public void testDownvoteTopicNonExistentTopic() {
		TopicRepoImpl tr = new TopicRepoImpl();
		tr.downvoteTopic("topic1");
		//Since no topic present, vote should be ignored
		assertTrue(tr.getTopTopics(1).size() == 0);
	}

}
