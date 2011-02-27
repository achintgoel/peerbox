package network;

import java.net.InetSocketAddress;
import java.net.URI;

import org.jboss.netty.channel.Channel;

public class MessageSender {
	final protected Channel channel;
	
	MessageSender(final Channel channel) {
		this.channel = channel;
	}
	
	protected void sendData(URI destination, String data) {
		channel.write(data, new InetSocketAddress(destination.getHost(), destination.getPort()));
	}
}
