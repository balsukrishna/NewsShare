package krishna.newsshare.datastructure;

public class VotedTopicImpl implements VotedTopic {
	
	private final String topic;
	private int upvotes;
	private int downvotes; 
	 
	public VotedTopicImpl(String topic) {
		this.topic = topic;
		this.upvotes = 0;
		this.downvotes = 0;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public int getUpvotes() {
		return upvotes;
	}
	
	public void incrementUpvote() {
		upvotes++;
	}

	public void incrementDownvote() {
		downvotes++;
	}


	@Override
	public int getDownvotes() {
		return downvotes;
	}
	
}
