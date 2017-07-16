package krishna.newsshare.datastructure;

import java.util.List;

/**
 * Topic Repo is single place where all the topics and votes are stored.
 * It is not thread-safe.
 * 
 * It also provides methods to read/write repo
 * @author krishna
 *
 */
public interface TopicRepo {

	/**
	 * Returns top topics 
	 */
	public List<VotedTopic> getTopTopics();
	
	
	/**
	 * Add topic to repo. No action if topic already exists
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
