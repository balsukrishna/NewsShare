package krishna.newsshare.datastructure;

/**
 * Encapsulates single update sent by client
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
	
	/**
	 * the type of update can be identified by updatetype
	 * @author krishna
	 *
	 */
	public static enum UpdateType {
		/**
		 * Indicates the update is a new topic
		 */
		TOPIC,
		/**
		 * The update is upvoting a topic
		 */
		UPVOTE,
		/**
		 * The update is downvoting a topic
		 */
		DOWNVOTE
	}
}
