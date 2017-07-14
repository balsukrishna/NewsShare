package krishna.newsshare.datastructure;

public interface VotedTopic extends Comparable<VotedTopic>{
	
	public String getTopic();
	
	public int getVotes();
	
	@Override
	default public int compareTo(VotedTopic o) {
		return getVotes() - o.getVotes();
	}
}