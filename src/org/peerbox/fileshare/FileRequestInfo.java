package org.peerbox.fileshare;

import java.net.URI;

public class FileRequestInfo {
	protected URI fromURI;
	protected long expiration;
	protected String filePath;
	public FileRequestInfo(URI fromURI, long expiration, String path) {
		super();
		this.fromURI = fromURI;
		this.expiration = expiration;
		this.filePath = path;
	}
	public URI getFromURI() {
		return fromURI;
	}
	public void setFromURI(URI fromURI) {
		this.fromURI = fromURI;
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
