package krishna.newsshare.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Check if datastructure used is optimal
 * @author krishna
 *
 */
public class TopicRepoImpl implements TopicRepo {
	
	public Map<String,VotedTopicImpl> topicMap;
	
	public TopicRepoImpl() {
		topicMap = new HashMap<>();
	}

	@Override
	public List<VotedTopic> getTopTopics(int n) {
		ArrayList<VotedTopicImpl> votedTopics = new ArrayList<>(topicMap.values());
		Collections.sort(votedTopics,Collections.reverseOrder());
		
		List<VotedTopic> topTopics = new ArrayList<>();
		int i = 0;
		for(VotedTopic vt : votedTopics) {
			topTopics.add(vt);
			i++;
			if(i == n) {
				break;
			}
		}
		return topTopics;
	}

	@Override
	public void addTopic(String topic) {
		if(topicMap.get(topic) == null) {
			VotedTopicImpl vt = new VotedTopicImpl(topic);
			topicMap.put(topic, vt);
		}
	}

	@Override
	public void upvoteTopic(String topic) {
		VotedTopicImpl vt = topicMap.get(topic);
		if(vt != null) {
			vt.incrementUpvote();
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
