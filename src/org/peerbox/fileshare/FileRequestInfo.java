package org.peerbox.fileshare;

import java.net.URI;

public class FileRequestInfo {
	protected long expiration;
	protected String filePath;
	public FileRequestInfo(long expiration, String path) {
		super();
		this.expiration = expiration;
		this.filePath = path;
	}
	public long getExpiration() {
		return expiration;
	}
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
