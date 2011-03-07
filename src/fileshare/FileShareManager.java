package fileshare;



import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import rpc.RPCEvent;
import rpc.RPCHandler;
import rpc.RPCResponseListener;

import com.google.gson.Gson;

import fileshare.messages.FileRequest;
import fileshare.messages.FileResponse;
import fileshare.messages.Request;
import fileshare.messages.Response;
import fileshare.messages.SharedDirectoryRequest;
import fileshare.messages.SharedDirectoryResponse;
import friendpeer.Friend;

public class FileShareManager {
	
	protected File mySharedDirectory;
	protected HashMap<String, FileRequestInfo> requestIDtoFileRequest; 
	
	public FileShareManager(String sharedPathName) {
		mySharedDirectory = new File(sharedPathName);
		requestIDtoFileRequest = new HashMap<String, FileRequestInfo>();
	}
	
	public RPCHandler getRPC() {
		return null; //TODO
	}
	
	//TODO: handle OS specific slashes
	public FileInfo[] getSharedContents(String relativePath) {
		File requestDir = null;
		if(!relativePath.isEmpty()) {
			requestDir = new File(mySharedDirectory.getAbsolutePath().concat(relativePath));
		}
		else {
			requestDir = new File(mySharedDirectory.getAbsolutePath());
		}
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
			e.printStackTrace();
		}
		return null;
	}
	
	protected <T extends Response> void sendRequestRPC(Friend targetFriend, Request request, final Class<T> responseClass, final ResponseListener<T> callback) {
		final Gson gson = new Gson();
		String requestData = gson.toJson(request);
		getRPC().sendRequest(targetFriend.getNetworkAddress(), "fileshare", requestData, new RPCResponseListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onResponseReceived(RPCEvent event) {
				try {
					callback.onResponseReceived((T) gson.fromJson(event.getDataString(), responseClass));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void getSharedDirectory(String relativePath, Friend friend, ResponseListener<SharedDirectoryResponse> response) {
		SharedDirectoryRequest request = new SharedDirectoryRequest(friend, relativePath);
		this.sendRequestRPC(friend, request, SharedDirectoryResponse.class, response);
	}

	public void getFile(String relativePath, Friend friend, FileInfo file, ResponseListener<FileResponse> response) {
		FileRequest request = new FileRequest(friend, file, relativePath);
		this.sendRequestRPC(friend, request, FileResponse.class, response);
	}
	public void setRequestIDtoFileRequest(String relativePath, String requestID, final String filename, Date expiration, URI requestFrom) {
		File requestDir = new File(mySharedDirectory.getAbsolutePath().concat(relativePath));
		File[] files = requestDir.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				if(filename.equals(arg1))
					return true;
				else
					return false;
			}
			
		});
		if(files.length == 1) {
			try {
				FileRequestInfo fileInfo = new FileRequestInfo(requestFrom, expiration, files[0].getCanonicalPath());
				requestIDtoFileRequest.put(requestID, fileInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public String getFilePath(URI uri) {
		String requestID = uri.getPath();
		if(requestIDtoFileRequest.get(requestID).getExpiration().after(Calendar.getInstance().getTime())){
			return requestIDtoFileRequest.get(requestID).getFilePath();
		}
		return null;

	}
	
}
