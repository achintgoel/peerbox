package org.peerbox.network.http;

public interface HttpClientListener {
	public void finished();
	public void downloadError();
	public void started();
	public void localFileError();
}
