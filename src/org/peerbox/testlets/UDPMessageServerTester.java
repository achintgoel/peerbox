package org.peerbox.testlets;
import org.peerbox.network.IncomingMessage;
import org.peerbox.network.MessageListener;
import org.peerbox.network.MessageServerHandler;


public class UDPMessageServerTester {
	public static void main(String[] args) {
		MessageServerHandler.startUDPServer(5555, new PrintMessages());
	}
}

class PrintMessages implements MessageListener {
	@Override
	public void onMessage(IncomingMessage message) {
		System.out.println(message.getSenderURI().toString());
		System.out.println(message.getDataString());
	}
}