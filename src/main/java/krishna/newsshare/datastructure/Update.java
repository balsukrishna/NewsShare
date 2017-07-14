package krishna.newsshare.datastructure;

/**
 * Encapsulates the update sent by client
 * @author krishna
 *
 */
public class Update {
	
	private final UpdateType updateType;
	private final String name;
	
	public Update(String topic,UpdateType type) {
		this.updateType = type;
		this.name = topic;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public String getName() {
		return name;
	}
	
	public static enum UpdateType {
		TOPIC,
		UPVOTE,
		DOWNVOTE
	}
}
