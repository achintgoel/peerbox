package org.peerbox.demo;

import org.peerbox.demo.cli.ExtendableCLI;
import org.peerbox.demo.cli.FileShareCLIHandler;
import org.peerbox.demo.cli.FriendCLIHandler;
import org.peerbox.fileshare.FileShareManager;
import org.peerbox.friend.FriendManager;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.security.SecureMessageHandler;

public class FileShareCLI {
	public static void main(String[] args) {
		RPCHandler rpc = RPCHandler.getUDPInstance(8002);
		NetworkInstance network = new NetworkInstance(rpc);
		FriendManager friendManager = new FriendManager(network.getSingleMap("users"), new SecureMessageHandler(), rpc.getLocalURI());
		FileShareManager fileShareManager = new FileShareManager(rpc);
		
		ExtendableCLI cli = new ExtendableCLI();
		cli.registerHandler("friend", new FriendCLIHandler(friendManager));
		cli.registerHandler("fileshare", new FileShareCLIHandler(fileShareManager, friendManager));
		cli.registerAlias("addFriend", "friend add");
		cli.start();
	}
}
