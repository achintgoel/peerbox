package rpc;

public class RPCMessage {
	protected String pbox;
	protected String service;
	protected int id;
	protected String request;
	protected String response;
	
	/**
	 * 
	 * @param version
	 * @param service
	 * @param id
	 * @param request
	 * @param response
	 */
	public RPCMessage(String version, String service, int id, String request, String response) {
		this.pbox = version;
		this.service = service;
		this.id = id;
		this.request = request;
		this.response = response;
	}
	
	public String getVersion() {
		return pbox;
	}
	
	public String getService() {
		return service;
	}
	
	public int getId() {
		return id;
	}
	
	public String getRequest() {
		return request;
	}
	
	public String getResponse() {
		return response;
	}
}
