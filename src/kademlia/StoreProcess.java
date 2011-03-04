package kademlia;

import java.util.List;

import kademlia.messages.FindNodeResponse;
import kademlia.messages.StoreRequest;
import kademlia.messages.StoreResponse;

//TODO: must execute a FindNode to get the closest nodes prior to running store operation.

/**
 * Storing a key value pair in the k closest nodes
 * 
 *
 */
public class StoreProcess {
	
	protected final int threshhold;
	protected final NetworkInstance networkInstance;
	protected final StoreRequest request;
	protected final ResponseListener<StoreResponse> callback;
	protected List<Node> recipients;
	protected int successes;
	protected int failures;
	
	
	/**
	 * threshhold - Number of Nodes store should be successful to return success
	 * 				(half the number of recipients)
	 * @param ni
	 * @param request
	 * @param responseListener
	 */
	private StoreProcess(NetworkInstance ni, StoreRequest request, ResponseListener<StoreResponse> responseListener){
		this.networkInstance = ni;
		this.request = request;
		this.callback = responseListener;
		this.recipients = null;
		this.threshhold = recipients.size()/2;
		this.successes = 0;
		this.failures = 0;
	}
	
	
	/**
	 * function called to initiate and run the request 
	 * @param ni NetworkInstance
	 * @param request the storerequest
	 * @param responseListener its callback that receives the final confirmation or failure 
	 */
	
	public static void execute(NetworkInstance ni, StoreRequest request, ResponseListener<StoreResponse> responseListener){
		StoreProcess sp = new StoreProcess(ni, request, responseListener);
		sp.performSearch();
	}
	
	private void performSearch(){
		networkInstance.findNode(request.getKey().getIdentifier(), new ResponseListener<FindNodeResponse>(){
			@Override
			public void onFailure() {
				callback.onFailure();				
			}

			public void onResponseReceived(FindNodeResponse response) {
				recipients = response.getNearbyNodes();
				performStore();
			}
			
		});
	}
	
	private void performStore(){
		for(Node node : recipients){
			networkInstance.sendRequestRPC(node, request, StoreResponse.class, new ResponseListener<StoreResponse>(){

				@Override
				public void onFailure() {
					failures++;
					makeCallback();
				}

				@Override
				public void onResponseReceived(StoreResponse response) {
					successes++;
					makeCallback();
				}
				
				private void makeCallback(){
					if(successes + failures == recipients.size()){
						if(successes >= threshhold){
							callback.onResponseReceived(new StoreResponse(true));
						}
						else{
							callback.onFailure();
						}
					}
				}
				
			});
		}
	}

}
