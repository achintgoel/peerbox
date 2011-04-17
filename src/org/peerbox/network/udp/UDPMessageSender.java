package org.peerbox.network.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.jboss.netty.channel.Channel;
import org.peerbox.network.MessageSender;

class UDPMessageSender implements MessageSender {
	final protected Channel channel;
	final String protocol;
	
	public UDPMessageSender(final Channel channel, final String protocol) {
		this.channel = channel;
		this.protocol = protocol;
	}
	
	@Override
	public void sendData(URI destination, String data) {
		channel.write(data, new InetSocketAddress(destination.getHost(), destination.getPort()));
//		System.out.println(System.currentTimeMillis() + " [S]: " + destination.toString() + ": " + data); //DEBUG, TODO: REMOVE
	}
	
	/**
	 * Can return null if local ip address is unknown
	 * Will only return a single IP address even if we are bound to multiple
	 * @return
	 */
	@Override
	public URI getLocalURI() {
		InetSocketAddress addr = (InetSocketAddress) channel.getLocalAddress();
    	URI uri;
    	try {
			try {
				uri = new URI(protocol + "://" + InetAddress.getLocalHost().getHostAddress() + ":" + addr.getPort());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				uri = null;
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			uri = null;
		}
		return uri;
	}
}
