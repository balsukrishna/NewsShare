package krishna.newsshare.datastructure;

import java.util.List;

public interface TopicRepo {

	/**
	 * Returns top n topics ordered by votes desc 
	 * @param n
	 */
	public List<VotedTopic> getTopTopics(int n);
	
	
	/**
	 * Add topic to repo. No action if topic already added
	 * @param topic
	 */
	public void addTopic(String topic);
	

	/**
	 * Upvotes a topic. No action if topic not present
	 * @param topic
	 */
	public void upvoteTopic(String topic);
	
	/**
	 * Downvotes a topic. No action if topic not present
	 * @param topic
	 */
	public void downvoteTopic(String topic);
	
}
