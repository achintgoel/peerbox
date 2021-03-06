package org.peerbox.network.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.peerbox.fileshare.FileShareManager;

public class HttpStaticFileServer {
	public HttpStaticFileServer(int port, FileShareManager manager) {
		// Configure the server
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory
		bootstrap.setPipelineFactory(new HttpStaticFileServerPipelineFactory(manager));

		// Bind and start to accept incoming connections
		bootstrap.bind(new InetSocketAddress(port));
	}

}
