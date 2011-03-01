package fileshare;



import rpc.RPCEvent;
import rpc.RPCHandler;
import rpc.RPCResponseListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fileshare.messages.Request;
import fileshare.messages.Response;
import fileshare.messages.SharedDirectoryRequest;
import fileshare.messages.SharedDirectoryResponse;
import friendpeer.Friend;

public class FileShareManager {
	
	
	public RPCHandler getRPC() {
		return null; //TODO
	}
	
	protected <T extends Response> void sendRequestRPC(Friend targetFriend, Request request, final ResponseListener<T> callback) {
		final Gson gson = new Gson();
		String requestData = gson.toJson(request);
		getRPC().sendRequest(targetFriend.getNetworkAddress(), "fileshare", requestData, new RPCResponseListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onResponseReceived(RPCEvent event) {
				try {
					callback.onResponseReceived((T) gson.fromJson(event.getDataString(), new TypeToken<T>(){}.getType()));
				} catch (Exception e) {
					//
				}
			}
		});
	}
	
	public void getSharedDirectory(Friend friend, ResponseListener<SharedDirectoryResponse> response) {
		SharedDirectoryRequest request = new SharedDirectoryRequest(friend);
		this.sendRequestRPC(friend, request, response);
	}

}
