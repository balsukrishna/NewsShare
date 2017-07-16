package krishna.newsshare.datastructure;

/**
 * VotedTopic encapsulates a topic and it's corresponding upvotes and downvotes.<br>
 * Two VotedTopics are compared by comparing their upvotes
 * @author krishna
 *
 */
public interface VotedTopic extends Comparable<VotedTopic>{
	
	/**
	 * @return Returns topic name
	 */
	public String getTopic();
	
	/**
	 * @return Returns no of upvotes
	 */
	public int getUpvotes();
	
	/**
	 * @return Returns no of downvotes
	 */
	public int getDownvotes();
	
	@Override
	default public int compareTo(VotedTopic o) {
		return getUpvotes() - o.getUpvotes();
	}
}