package org.peerbox.fileshare.messages;

import org.peerbox.friendpeer.Friend;
import org.peerbox.kademlia.messages.Message;


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
