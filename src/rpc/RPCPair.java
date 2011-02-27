package rpc;

import network.MessageSender;
import network.MessageServerHandler;

public class RPCPair {
	final protected RPCHandler server;
	final protected RPCClient client;
	
	public RPCPair(RPCHandler server, RPCClient client) {
		this.server = server;
		this.client = client;
	}
	
	public static RPCPair getUDPInstance(int port) {
		RPCHandler rpcServer = new RPCHandler();
		MessageSender sender = MessageServerHandler.startUDPServer(port, rpcServer.newListener());
		RPCClient rpcClient = new RPCClient(sender);
		RPCPair rpcPair = new RPCPair(rpcServer, rpcClient);
		return rpcPair;
	}
	
	public RPCHandler getServer() {
		return server;
	}
	
	public RPCClient getClient() {
		return client;
	}
}
