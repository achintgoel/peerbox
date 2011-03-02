package fileshare.messages;

import fileshare.FileInfo;
import friendpeer.Friend;

public class FileRequest extends Request{
	protected Friend targetFriend;
	protected FileInfo file;
	final static public String command = "get_file";
	
	public FileRequest(Friend targetFriend, FileInfo file) {
		super();
		this.targetFriend = targetFriend;
		this.file = file;
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

}
