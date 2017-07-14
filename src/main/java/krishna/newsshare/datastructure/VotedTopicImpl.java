package krishna.newsshare.datastructure;

public class VotedTopicImpl implements VotedTopic {
	
	private final String topic;
	private int votes;
	 
	public VotedTopicImpl(String topic) {
		this.topic = topic;
		this.votes = 0;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public void incrementVote() {
		votes++;
	}

	public void decrementVote() {
		votes--;
	}
	
}
