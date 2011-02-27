package rpc;

import com.google.gson.JsonElement;

public interface RPCEvent {
	public void respond(Object data);
	
	@Deprecated
	public JsonElement getJsonElement();
	
	public String getDataString();
}
