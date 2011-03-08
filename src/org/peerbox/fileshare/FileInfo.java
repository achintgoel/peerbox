package org.peerbox.fileshare;

public class FileInfo {
	protected String name;
	protected String type;
	protected long size;
	
	public FileInfo(String name, String type, long size) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
	}
	protected FileInfo() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
