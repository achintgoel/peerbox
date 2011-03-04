package fileshare.messages;


import fileshare.FileInfo;

public class SharedDirectoryResponse extends Response{
	protected FileInfo[] contents;

	public SharedDirectoryResponse(FileInfo[] contents) {
		super();
		this.contents = contents;
	}
	protected SharedDirectoryResponse() {
		
	}

	public FileInfo[] getContents() {
		return contents;
	}
	
	
}
