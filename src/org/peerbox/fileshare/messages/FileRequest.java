package org.peerbox.fileshare.messages;

import org.peerbox.fileshare.FileInfo;
import org.peerbox.friendpeer.Friend;


public class FileRequest extends Request{
	protected Friend targetFriend;
	protected FileInfo file;
	protected String relativePath;
	
	final static public String COMMAND = "get_file";
	
	public FileRequest(Friend fromFriend, Friend targetFriend, FileInfo file, String relativePath) {
		super();
		this.relativePath = relativePath;
		this.fromFriend = fromFriend;
		this.targetFriend = targetFriend;
		this.file = file;
		this.command = COMMAND;
	}
	protected FileRequest() {
		
	}
	
	public FileInfo getFile() {
		return file;
	}

	public void setFile(FileInfo file) {
		this.file = file;
	}

	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}
	
	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}


}
