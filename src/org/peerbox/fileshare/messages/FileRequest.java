package org.peerbox.fileshare.messages;

import org.peerbox.fileshare.FileInfo;
import org.peerbox.friend.Friend;


public class FileRequest extends Request{
	protected String file;
	protected String relativePath;
	
	final static public String COMMAND = "get_file";
	
	public FileRequest(String file, String relativePath) {
		super();
		this.relativePath = relativePath;
		this.file = file;
		this.command = COMMAND;
	}
	protected FileRequest() {
		
	}
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
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
