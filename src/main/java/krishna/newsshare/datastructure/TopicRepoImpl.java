package krishna.newsshare.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * TODO Check if datastructure used is optimal
 * @author krishna
 *
 */
public class TopicRepoImpl implements TopicRepo {
	
	public Map<String,VotedTopicImpl> topicMap;
	public PriorityQueue<VotedTopicImpl> topTopics;
	public int noOfTopTopics;
	
	public TopicRepoImpl(int numberOfTops) {
		topicMap = new HashMap<>();
		topTopics = new PriorityQueue<>();
		noOfTopTopics = numberOfTops;
	}

	@Override
	public List<VotedTopic> getTopTopics() {
		ArrayList<VotedTopic> votedTopics = new ArrayList<>(topTopics);
		return votedTopics;
	}

	@Override
	public void addTopic(String topic) {
		//If not present, add.
		if(topicMap.get(topic) == null) {
			VotedTopicImpl vt = new VotedTopicImpl(topic);
			topicMap.put(topic, vt);
			
			//If total number of topics < allowed top topics
			//Simply add it to top topics
			if(topTopics.size() < noOfTopTopics) {
				topTopics.add(vt);
			}
		}		
	}
	

	@Override
	public void upvoteTopic(String topic) {
		VotedTopicImpl vt = topicMap.get(topic);
		if(vt != null) {
			vt.incrementUpvote();
			
			//Doesn't exist in top topics?
			
			//Constant operation log(noOfTopTopics)
			if(!topTopics.contains(vt)) {
				//Constant operation
				VotedTopicImpl min = topTopics.peek();
				//Check against min vote topic and replace it if needed
				if(min.getUpvotes() < vt.getUpvotes()) {
					//Constant operations log(noOfTopTopics)
					topTopics.poll();
					topTopics.add(vt);
				}
			}	
		}
	}

	@Override
	public void downvoteTopic(String topic) {
		VotedTopicImpl vt = topicMap.get(topic);
		if(vt != null) {
			vt.incrementDownvote();
		}
	}
	
	
}
