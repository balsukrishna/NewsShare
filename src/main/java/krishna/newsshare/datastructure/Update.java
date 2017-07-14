package krishna.newsshare.datastructure;

/**
 * Encapsulates the update sent by client
 * @author krishna
 *
 */
public class Update {
	
	private final UpdateType updateType;
	private final String topic;
	
	public Update(String topic,UpdateType type) {
		this.updateType = type;
		this.topic = topic;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public String getTopic() {
		return topic;
	}
	
	public static enum UpdateType {
		TOPIC,
		UPVOTE,
		DOWNVOTE
	}
}
