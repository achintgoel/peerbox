package fileshare.messages;

import fileshare.FileInfo;
import friendpeer.Friend;

public class FileRequest extends Request{
	protected Friend targetFriend;
	protected FileInfo file;
	protected String relativePath;
	
	final static public String command = "get_file";
	
	public FileRequest(Friend targetFriend, FileInfo file, String relativePath) {
		super();
		this.relativePath = relativePath;
		this.targetFriend = targetFriend;
		this.file = file;
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
