package org.peerbox.fileshare.messages;

import org.peerbox.fileshare.FileInfo;

public class SharedDirectoryResponse extends Response {
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
