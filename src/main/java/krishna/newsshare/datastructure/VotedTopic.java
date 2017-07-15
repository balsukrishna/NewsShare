package krishna.newsshare.datastructure;

public interface VotedTopic extends Comparable<VotedTopic>{
	
	public String getTopic();
	
	public int getUpvotes();
	
	public int getDownvotes();
	
	@Override
	default public int compareTo(VotedTopic o) {
		return getUpvotes() - o.getUpvotes();
	}
}