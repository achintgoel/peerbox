package org.peerbox.fileshare;

import java.net.URI;
import java.util.Date;

public class FileRequestInfo {
	protected URI fromURI;
	protected Date expiration;
	protected String filePath;
	public FileRequestInfo(URI fromURI, Date expiration, String path) {
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
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
