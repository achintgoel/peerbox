package rpc;

import com.google.gson.JsonElement;

public interface RPCEvent {
	public void respond(String data);
	
	@Deprecated
	public JsonElement getJsonElement();
	
	public String getDataString();
	
	public String getServiceName();
}
