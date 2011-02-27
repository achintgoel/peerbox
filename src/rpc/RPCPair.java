package rpc;

import network.MessageSender;
import network.MessageServerHandler;

public class RPCPair {
	final protected RPCServer server;
	final protected RPCClient client;
	
	public RPCPair(RPCServer server, RPCClient client) {
		this.server = server;
		this.client = client;
	}
	
	public static RPCPair getUDPInstance(int port) {
		RPCServer rpcServer = new RPCServer();
		MessageSender sender = MessageServerHandler.startUDPServer(port, rpcServer.newListener());
		RPCClient rpcClient = new RPCClient(sender);
		RPCPair rpcPair = new RPCPair(rpcServer, rpcClient);
		return rpcPair;
	}
	
	public RPCServer getServer() {
		return server;
	}
	
	public RPCClient getClient() {
		return client;
	}
}
