package org.peerbox.testlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import java.util.Scanner;

import org.peerbox.fileshare.FileInfo;
import org.peerbox.fileshare.FileShareManager;
import org.peerbox.fileshare.ResponseListener;
import org.peerbox.fileshare.messages.FileResponse;
import org.peerbox.fileshare.messages.SharedDirectoryResponse;
import org.peerbox.friend.Friend;
import org.peerbox.friend.FriendManager;
import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.network.http.HttpClient;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.security.SecureMessageHandler;


public class FileShareTest {
	public static void main(String[] args) {
		int port;
		if (args.length < 1) {
			System.out.println("error, please specify port");
			return;
		} else {
			port = Integer.parseInt(args[0]);
		}
		LinkedList<URI> startURIs = new LinkedList<URI>();
		for (int i = 1; i < args.length; i++) {
			try {
				startURIs.add(new URI("udp://"+args[i]));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		final RPCHandler rpcHandle = RPCHandler.getUDPInstance(port);
		final NetworkInstance instance = new NetworkInstance(rpcHandle);
		System.out.println("Network instance created: " + instance.getLocalNodeIdentifier());

		if (startURIs.size() > 0) {
			instance.bootstrap(startURIs, new BootstrapListener() {
	
				@Override
				public void onBootstrapSuccess() {
					System.out.println("Bootstrap Complete");
				}
	
				@Override
				public void onBootstrapFailure() {
					System.out.println("Bootstrap Failed");	
				}
			});
		}
		final FriendManager manager = new FriendManager(instance.getSingleMap("users"), new SecureMessageHandler());
		manager.signOn(rpcHandle.getLocalURI());
		System.out.println("Signed on properly");
		final FileShareManager fsm = new FileShareManager("/home/rajiv/Desktop", rpcHandle);
		Scanner scan = new Scanner(System.in);
		int count = 0;
		while(true){
			try{
				String function = scan.next();
				if(function.equals("addFriend") && scan.hasNextBigInteger()) {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(scan.nextBigInteger().toByteArray());
					 KeyFactory keyFactory = KeyFactory.getInstance("DSA");
					 PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
					 manager.createFriend("newFriend"+count, null, pubKey);
					 count++;
				}
				if(function.equals("printBuddys")) {
					manager.printBuddyList();
				}
				if(function.equals("setSharedDirectory")  && scan.hasNext()) {
					fsm.setFilePath(scan.next());
				}
				if(function.equals("getSharedContents")  && scan.hasNext()) {
					Friend target = manager.getFriend(scan.next());
					if(target != null) {
						
						String relativePath = "";
						if(scan.hasNext()){
							//System.out.println("HIIIII");
							relativePath = scan.next();
						}

						fsm.getSharedDirectory(relativePath, target, new ResponseListener<SharedDirectoryResponse>() {

							@Override
							public void onFailure() {
								// TODO Auto-generated method stub
								System.out.println("Shared contents failed!!!");
							}

							@Override
							public void onResponseReceived(
									SharedDirectoryResponse response) {
								// TODO Auto-generated method stub
								//System.out.println("Received a response!!!!");
								if(response.getContents() != null) {
									FileInfo[] contents = response.getContents();
									for(int i=0; i<contents.length;i++){
										System.out.println(contents[i].getName());
									}
								}
								else{
									System.out.println("The directory asked for does not exist!");
								}
								
							}
							
						});
					}
					else {
						System.out.println("ALIAS NOT FOUND");
					}
					
				}
				if(function.equals("getFile") && scan.hasNext()) {
					Friend target = manager.getFriend(scan.next());
					if(target != null) {
						final String file;
						if(scan.hasNext()) {
							file = scan.next();
							fsm.getFile("", target, file, new ResponseListener<FileResponse>() {

								@Override
								public void onFailure() {
									
									
								}

								@Override
								public void onResponseReceived(
										FileResponse response) {
									System.out.println("RECEIVED RESPONSE TO FILE REQUEST");
									HttpClient http_client = new HttpClient(response.getFileLocURI(), file);
									
								}
								
							});
						}
					}
					else {
						System.out.println("ALIAS NOT FOUND");
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
