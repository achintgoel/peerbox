package network;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.channel.Channel;

public class MessageSender {
	final protected Channel channel;
	final String protocol;
	
	MessageSender(final Channel channel, final String protocol) {
		this.channel = channel;
		this.protocol = protocol;
	}
	
	public void sendData(URI destination, String data) {
		channel.write(data, new InetSocketAddress(destination.getHost(), destination.getPort()));
//		System.out.println(System.currentTimeMillis() + " [S]: " + destination.toString() + ": " + data); //DEBUG, TODO: REMOVE
	}
	
	public URI getLocalURI() {
		InetSocketAddress addr = (InetSocketAddress) channel.getLocalAddress();
    	URI uri;
    	try {
			uri = new URI(protocol + "://" + addr.getHostName() + ":" + addr.getPort());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			uri = null;
		}
		return uri;
	}
}
