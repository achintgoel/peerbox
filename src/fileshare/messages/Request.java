package fileshare.messages;

import kademlia.messages.Message;
import friendpeer.Friend;

public abstract class Request extends Message{
	public Friend fromFriend;
	
	public abstract String getCommand();

	public Friend getFromFriend() {
		return fromFriend;
	}
	
	

}
