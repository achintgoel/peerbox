package org.peerbox.kademlia;

import java.util.List;

import org.peerbox.kademlia.messages.FindNodeResponse;
import org.peerbox.kademlia.messages.StoreRequest;
import org.peerbox.kademlia.messages.StoreResponse;

//TODO: must execute a FindNode to get the closest nodes prior to running store operation.

/**
 * Storing a key value pair in the k closest nodes
 * 
 * 
 */
public class StoreProcess {

	protected int threshold;
	protected final NetworkInstance networkInstance;
	protected final StoreRequest request;
	protected final ResponseListener<StoreResponse> callback;
	protected List<Node> recipients;
	protected int successes;
	protected int failures;

	/**
	 * threshhold - Number of Nodes store should be successful to return success
	 * (half the number of recipients)
	 * 
	 * @param ni
	 * @param request
	 * @param responseListener
	 */
	private StoreProcess(NetworkInstance ni, StoreRequest request, ResponseListener<StoreResponse> responseListener) {
		this.networkInstance = ni;
		this.request = request;
		this.callback = responseListener;
		this.recipients = null;
		this.threshold = networkInstance.getConfiguration().getAlpha();
		this.successes = 0;
		this.failures = 0;
	}

	/**
	 * function called to initiate and run the request
	 * 
	 * @param ni
	 *            NetworkInstance
	 * @param request
	 *            the storerequest
	 * @param responseListener
	 *            its callback that receives the final confirmation or failure
	 */

	public static void execute(NetworkInstance ni, StoreRequest request,
			ResponseListener<StoreResponse> responseListener) {
		StoreProcess sp = new StoreProcess(ni, request, responseListener);
		sp.performSearch();
	}

	private void performSearch() {
		networkInstance.findNode(request.getKey().getIdentifier(), false, new ResponseListener<FindNodeResponse>() {
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

	private void performStore() {
		if (recipients.size() > 0 && recipients.size() < threshold) {
			threshold = recipients.size();
		}
		for (Node node : recipients) {
			networkInstance.sendRequestRPC(node, request, StoreResponse.class, new ResponseListener<StoreResponse>() {

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

				private void makeCallback() {
					if (successes + failures == recipients.size()) {
						if (successes >= threshold) {
							callback.onResponseReceived(new StoreResponse(true));
						} else {
							callback.onFailure();
						}
					}
				}

			});
		}
	}

}
