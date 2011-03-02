package fileshare.messages;

import java.net.URI;

public class FileResponse extends Response{
	protected URI fileLocURI;
	protected String requestID;
	public FileResponse(URI fileLocURI, String requestID) {
		super();
		this.fileLocURI = fileLocURI;
		this.requestID = requestID;
	}
	public URI getFileLocURI() {
		return fileLocURI;
	}
	public void setFileLocURI(URI fileLocURI) {
		this.fileLocURI = fileLocURI;
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	

}
