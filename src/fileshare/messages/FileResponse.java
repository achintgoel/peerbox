package fileshare.messages;

import java.net.URI;

public class FileResponse extends Response{
	protected URI fileReqURI;
	public FileResponse(URI fileLocURI) {
		super();
		this.fileReqURI = fileLocURI;

	}
	public URI getFileLocURI() {
		return fileReqURI;
	}
	public void setFileLocURI(URI fileLocURI) {
		this.fileReqURI = fileLocURI;
	}
	

}
