package network;

import java.net.URI;

import org.jboss.netty.channel.MessageEvent;

public class IncomingMessage {
	protected URI sender;
	protected String data;
	protected MessageEvent origMessageEvent;
	
	IncomingMessage(MessageEvent origMessageEvent, URI sender, String data) {
		this.origMessageEvent = origMessageEvent;
		this.data = data;
		this.sender = sender;
	}
	
	public URI getSenderURI() {
		return sender;
	}
	
	public String getDataString() {
		return data;
	}
	
	public void sendResponse(String responseData) {
		origMessageEvent.getChannel().write(responseData, origMessageEvent.getRemoteAddress());
	}
}
