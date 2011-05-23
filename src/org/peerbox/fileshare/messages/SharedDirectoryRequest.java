package org.peerbox.fileshare.messages;

import org.peerbox.fileshare.messages.Request;
import org.peerbox.friend.Friend;

public class SharedDirectoryRequest extends Request {
	protected String sharedRelativePath;

	final static public String COMMAND = "get_shared";

	public SharedDirectoryRequest(String relativePath) {
		this.sharedRelativePath = relativePath;
		this.command = COMMAND;
	}

	protected SharedDirectoryRequest() {

	}

	public String getCommand() {
		return command;
	}

	public String getSharedRelativePath() {
		return sharedRelativePath;
	}

	public void setSharedRelativePath(String sharedRelativePath) {
		this.sharedRelativePath = sharedRelativePath;
	}

}
