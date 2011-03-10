package org.peerbox.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.peerbox.demo.cli.ExtendableCLI;
import org.peerbox.demo.cli.FileShareCLIHandler;
import org.peerbox.demo.cli.FriendCLIHandler;
import org.peerbox.demo.cli.KadCLIHandler;
import org.peerbox.fileshare.FileShareManager;
import org.peerbox.friend.FriendManager;
import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.RPCResponseListener;
import org.peerbox.security.SecureMessageHandler;

public class FileShareCLI {
	private static String bindIP;
	private static int bindPort;
	private static List<URI> bootstrapURI = new LinkedList<URI>();
	private static NetworkInstance networkInstance;
	private static FriendManager friendManager;
	private static FileShareManager fileShareManager;
	
	public static void main(String[] args) {
		final RPCHandler rpc;
		try{
			if(args.length >= 2){
				for(int i = 1; i < args.length; i++){
					bootstrapURI.add(new URI("udp://"+args[i]));
				}
			}
			if(args.length < 1){
				printUsageAndExit();
			}
			else if(args.length >= 1){
				if(args[0].contains(":")){
					bindIP = args[0].substring(0, args[0].indexOf(":"));
					bindPort = Integer.parseInt(args[0].substring(args[0].indexOf(":") + 1));
					createInstance(null);
				}
				else{
					bindPort = Integer.parseInt(args[0]);
					rpc = RPCHandler.getUDPInstance(bindPort);
					rpc.sendRequest(new URI("udp://peerbox.org:20000"), "ipaddress", "", new RPCResponseListener(){

						@Override
						public void onResponseReceived(RPCEvent event) {
							bindIP = event.getDataString();
							System.out.println(bindIP);
							try {
								rpc.setLocalURI(new URI("udp://" + bindIP + ":" + bindPort));
							} catch (URISyntaxException e) {
								printUsageAndExit();
							}
							createInstance(rpc);
						}

						@Override
						public void onTimeout() {
							System.out.println("Could not obtain external IP");
							System.exit(0);
						}
						
					});
				}
			}
		}
		catch(Exception e){
			printUsageAndExit();
		}
	}
	
	private static void createInstance(RPCHandler rpc){
		if(rpc == null){
			rpc = RPCHandler.getUDPInstance(bindPort);
			try {
				rpc.setLocalURI(new URI("udp://" + bindIP + ":" + bindPort));
			} catch (URISyntaxException e) {
				System.out.println("Illegal URI syntax");
			}
		}		
		networkInstance = new NetworkInstance(rpc);
		if(!bootstrapURI.isEmpty()){
			networkInstance.bootstrap(bootstrapURI, new BootstrapListener(){
	
				@Override
				public void onBootstrapFailure() {
					System.out.println("Peerbox could not start");
				}
	
				@Override
				public void onBootstrapSuccess() {
					System.out.println("Welcome to peerbox");				
				}
				
			});
		}
		friendManager = new FriendManager(networkInstance.getSingleMap("users"), new SecureMessageHandler(), rpc.getLocalURI());
		fileShareManager = new FileShareManager(rpc);
		
		ExtendableCLI cli = new ExtendableCLI();
		cli.registerHandler("friend", new FriendCLIHandler(friendManager));
		cli.registerHandler("fileshare", new FileShareCLIHandler(fileShareManager, friendManager));
		cli.registerHandler("kad", new KadCLIHandler(networkInstance));
		cli.registerAlias("addFriend", "friend add");
		cli.start();
	}
	
	private static void printUsageAndExit() {
		System.out.println("Usage: [bind ip]:port [bootstrap ip:port]");
		System.exit(0);
	}
}
