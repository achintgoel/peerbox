package kademlia;

import java.net.URI;
import java.util.List;

import kademlia.messages.FindNodeResponse;
import kademlia.messages.PingResponse;


/**
 * Process created when joining the network 
 *
 */
public class BootstrapProcess {	
	
	protected final NetworkInstance ni;
	protected final List<URI> friends;
	protected final BootstrapListener callback;
	protected int successes;
	protected int failures;
	
	
	private BootstrapProcess(NetworkInstance networkInstance, List<URI> friends, BootstrapListener bootstrapListener){
		this.ni = networkInstance;
		this.friends = friends;
		this.callback = bootstrapListener;
		this.successes = 0;
		this.failures = 0;
	}
	
	/**
	 * The static function called to initialize the bootstrap process
	 * @param networkInstance
	 * @param friends list of URIs we send ping to
	 * @param bootstrapListener the callback from this process 
	 */
	public static void execute(NetworkInstance networkInstance, List<URI> friends, BootstrapListener bootstrapListener){
		BootstrapProcess bp = new BootstrapProcess(networkInstance, friends, bootstrapListener);
		bp.pingFriends();
	}
	
	/**
	 * Method that pings all friends and sends 
	 * an iterative findNode request to the first one
	 * 
	 */
	private void pingFriends(){
		for(final URI uri : friends){
			System.out.println("pinging!");
			Node node = new Node(uri);
			ni.ping(node, new ResponseListener<PingResponse>() {
				@Override
				public void onFailure() {
					System.out.println("bootstrap failure");
					failures++;
					if(failures == friends.size()){
						callback.onBootstrapFailure();
					}
				}

				@Override
				public void onResponseReceived(PingResponse response) {
					successes++;
					Identifier friendId = response.getMyNodeId();
					Node newNode = new Node(uri, friendId);
					ni.getBuckets().add(newNode);
					if(successes == 1){
						ni.findNode(ni.getLocalNodeIdentifier(), false, new ResponseListener<FindNodeResponse>(){
							public void onFailure() {
								System.out.println("bootstrap findnode failed");
							}
							public void onResponseReceived(FindNodeResponse response) {
								System.out.println("bootstrap findnode succeeded");
							}
						});
						callback.onBootstrapSuccess();
					}
				}
			});
		}
	}
}
