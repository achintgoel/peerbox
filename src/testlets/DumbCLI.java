package testlets;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Scanner;

import kademlia.BootstrapListener;
import kademlia.Identifier;
import kademlia.Key;
import kademlia.NetworkInstance;
import kademlia.Node;
import kademlia.ResponseListener;
import kademlia.messages.FindNodeResponse;
import kademlia.messages.FindValueResponse;
import kademlia.messages.PingResponse;
import kademlia.messages.StoreResponse;
import rpc.RPCHandler;

public class DumbCLI {

	/**
	 * @param args
	 */
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
				startURIs.add(new URI("udp://localhost:" + args[i]));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		final NetworkInstance instance = new NetworkInstance(RPCHandler.getUDPInstance(port));
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
		Scanner scan = new Scanner(System.in);
		while(true){
			try{
				String function = scan.next();
				if(function.equals("ping") && scan.hasNextBigInteger()){
					BigInteger ID = scan.nextBigInteger();
					System.out.println(ID);
					instance.ping(instance.getBuckets().findNodeByIdentifier
								(Identifier.fromBytes(ID.toByteArray())),
								new ResponseListener<PingResponse>(){
	
						@Override
						public void onFailure() {
							System.out.println("Ping failed");
						}
	
						@Override
						public void onResponseReceived(PingResponse response) {
							System.out.println("Ping Successful");
						}					
					});
				}
				else if(function.equals("findNode") && scan.hasNextBigInteger()){
					BigInteger ID = scan.nextBigInteger();				
					instance.findNode(Identifier.fromBytes(ID.toByteArray()), new ResponseListener<FindNodeResponse>(){
	
						@Override
						public void onFailure() {
							System.out.println("Find Node Failed");
						}
	
						@Override
						public void onResponseReceived(FindNodeResponse response) {
							if(response.isFound()){
								System.out.println("Found Node at: " + response.getFoundNode().getNetworkURI());
							}
							else{
								System.out.println("Did not find Node");
							}
							System.out.println("Nearest Nodes:-");
							for(Node node : response.getNearbyNodes()){
								System.out.println("\t" + node.getNetworkURI());
							}
						}
						
					});
				}
				else if(function.equals("findValue")){
					String key1 = scan.next();
					String key2 = scan.next();
					instance.findValue(new Key(key1, key2), new ResponseListener<FindValueResponse>(){
	
						@Override
						public void onFailure() {						
						}
	
						@Override
						public void onResponseReceived(FindValueResponse response) {
							if(response.isFound()){
								System.out.println("Found Value is: " + response.getFoundValue());
							}
							else{
								System.out.println("Did not find Value");
							}
							System.out.println("Nearest Nodes:-");
							for(Node node : response.getNearbyNodes()){
								System.out.println("\t" + node.getNetworkURI());
							}
						}	
					});
				}
				else if(function.equals("store")){
					String key1 = scan.next();
					String key2 = scan.next();
					String value = scan.next();
					instance.storeValue(new Key(key1, key2), value, true, new ResponseListener<StoreResponse>(){
	
						@Override
						public void onFailure() {
							System.out.println("Could not store the value");
						}
	
						@Override
						public void onResponseReceived(StoreResponse response) {
							if(response.successful){
								System.out.println("Value stored successfully");
							}
							else{
								System.out.println("Could not store the value");							
							}
						}
						
					});
				}
				else if(function.equals("exit")){
					System.exit(0);
				}
				else if(function.equals("printBuckets")){
					instance.getBuckets().print();
				}
				else{
					System.out.println("Wrong Command");				
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
