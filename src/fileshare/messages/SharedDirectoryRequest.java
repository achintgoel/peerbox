package fileshare.messages;

import fileshare.messages.Request;
import friendpeer.Friend;

public class SharedDirectoryRequest extends Request{
	protected Friend targetFriend;
	protected String sharedRelativePath;

	final static public String COMMAND = "get_shared";
	public SharedDirectoryRequest(Friend targetFriend, String relativePath) {
		this.targetFriend = targetFriend;
		this.sharedRelativePath = relativePath;
		this.command = COMMAND;
	}
	protected SharedDirectoryRequest(){
		
	}
	public Friend getTargetFriend() {
		return targetFriend;
	}
	public void setTagetFriend(Friend tagetFriend) {
		this.targetFriend = tagetFriend;
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
