package fileshare;

 

import java.util.Calendar;
import java.util.UUID;

import rpc.RPCEvent;
import rpc.ServiceRequestListener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fileshare.messages.FileRequest;
import fileshare.messages.FileResponse;
import fileshare.messages.Response;
import fileshare.messages.SharedDirectoryRequest;
import fileshare.messages.SharedDirectoryResponse;

public class FileshareRequestListener implements ServiceRequestListener{
	protected final Gson gson = new Gson();
	protected FileShareManager manager;
	
	public FileshareRequestListener(FileShareManager manager) {
		this.manager = manager;
	}

	@Override
	public void onRequestRecieved(RPCEvent e) {
		JsonParser parser = new JsonParser();
		
			final JsonObject root = (JsonObject) parser.parse(e.getDataString());
			String command = root.get("command").getAsString();
			//Request request;
			Response response = null;
			if(command.equals(SharedDirectoryRequest.COMMAND)){
				SharedDirectoryRequest fnr = gson.fromJson(root, SharedDirectoryRequest.class);
				FileInfo[] contents = manager.getSharedContents(fnr.getSharedRelativePath());
				if(contents == null){
					//response = new FindNodeResponse(ni.getBuckets().getNearestNodes(fnr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				else{
					response = new SharedDirectoryResponse(contents);
				}
				
			}
			else if(command.equals(FileRequest.command)) {
				FileRequest fnr = gson.fromJson(root, FileRequest.class);
				String requestId = UUID.randomUUID().toString();
				//TODO: generate URI to pass to client
				//TODO: figure out how to determine local IP address
				//URI uri = new URI("http://")
				//TODO: set the expiration date properly
				manager.setRequestIDtoFileRequest(fnr.getRelativePath(), requestId, fnr.getFile().getName(), Calendar.getInstance().getTime(), fnr.fromFriend.getNetworkAddress());
				response = new FileResponse(null);
			}
			if(response != null){
				String responseString = gson.toJson(response);
				e.respond(responseString);
			}
	}
		
	

}
