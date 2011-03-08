package testlets;

import java.net.URI;

import rpc.RPCEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fileshare.FileInfo;
import fileshare.FileShareManager;
import fileshare.FileshareRequestListener;
import fileshare.messages.FileRequest;
import fileshare.messages.FileResponse;
import fileshare.messages.SharedDirectoryRequest;
import fileshare.messages.SharedDirectoryResponse;

public class SharedFileTest {
	public static void main(String[] args) {
		final FileShareManager fsm = new FileShareManager("/home/rajiv/Desktop");
		FileshareRequestListener frl = new FileshareRequestListener(fsm);
		frl.onRequestRecieved(new RPCEvent(){

			@Override
			public String getDataString() {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				return gson.toJson(new SharedDirectoryRequest(null, ""));
			}

			@Override
			public URI getSenderURI() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServiceName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void respond(String data) {
				JsonParser parser = new JsonParser();
				
				final JsonObject root = (JsonObject) parser.parse(data);
				Gson gson = new Gson();
				SharedDirectoryResponse sdr = gson.fromJson(root, SharedDirectoryResponse.class);
				FileInfo[] contents = sdr.getContents();
				for(int i=0; i<contents.length;i++){
					System.out.println(contents[i].getName());
				}
				
				// TODO Auto-generated method stub
				
			}
			
		});
		
		frl.onRequestRecieved(new RPCEvent(){

			@Override
			public String getDataString() {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				return gson.toJson(new FileRequest(null, new FileInfo("hello.txt", "", 0), null));
			}

			@Override
			public URI getSenderURI() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServiceName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void respond(String data) {
				JsonParser parser = new JsonParser();
				
				final JsonObject root = (JsonObject) parser.parse(data);
				Gson gson = new Gson();
				FileResponse sdr = gson.fromJson(root, FileResponse.class);
				URI fileloc = sdr.getFileLocURI();
				
					System.out.println(fileloc.toString());
				
				
				// TODO Auto-generated method stub
				
			}
			
		});
	}

}
