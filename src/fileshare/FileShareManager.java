package fileshare;



import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

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
	
	File mySharedDirectory;
	
	public FileShareManager(String sharedPathName) {
		mySharedDirectory = new File(sharedPathName);
	}
	
	public RPCHandler getRPC() {
		return null; //TODO
	}
	
	//TODO: handle OS specific slashes
	public FileInfo[] getSharedContents(String relativePath) {
		File requestDir = new File(mySharedDirectory.getAbsolutePath().concat(relativePath));
		try {
			if(requestDir.getCanonicalPath().startsWith(mySharedDirectory.getAbsolutePath())) {
				FileFilter f = null;
				File[] contents = requestDir.listFiles(f);
				FileInfo[] contentInfo = new FileInfo[contents.length];
				for(int i = 0; i<contents.length; i++) {
					String name = contents[i].getName();
					String fileOrDir = null;
					if(contents[i].isFile()) {
						fileOrDir = "file";
					}
					else {
						fileOrDir = "directory";
					}
					long fileSize = contents[i].length();
					
					contentInfo[i] = new FileInfo(name, fileOrDir, fileSize);
				}
				return contentInfo;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
