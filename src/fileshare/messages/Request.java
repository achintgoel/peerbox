package fileshare.messages;

import kademlia.messages.Message;
import friendpeer.Friend;

public abstract class Request extends Message{
	public Friend fromFriend;
	protected String command;
	public String getCommand() {
		return command;
	}

	public Friend getFromFriend() {
		return fromFriend;
	}
	
	

}
