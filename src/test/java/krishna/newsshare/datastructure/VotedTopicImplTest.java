package krishna.newsshare.datastructure;

import static org.junit.Assert.*;

import org.junit.Test;

public class VotedTopicImplTest {

	@Test
	public void testVotedTopicImpl() {
		VotedTopicImpl vt = new VotedTopicImpl("topic");
		assertTrue(vt.getTopic().equals("topic"));
		assertTrue(vt.getUpvotes() == 0);
		assertTrue(vt.getDownvotes() == 0);
	}

	@Test
	public void testIncrementUpvote() {
		VotedTopicImpl vt = new VotedTopicImpl("topic");
		vt.incrementUpvote();
		vt.incrementUpvote();
		assertTrue(vt.getUpvotes() == 2);
		assertTrue(vt.getDownvotes() == 0);
	}

	@Test
	public void testIncrementDownvote() {
		VotedTopicImpl vt = new VotedTopicImpl("topic");
		vt.incrementDownvote();
		assertTrue(vt.getUpvotes() == 0);
		assertTrue(vt.getDownvotes() == 1);
	}

}
