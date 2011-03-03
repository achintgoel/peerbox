package network;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class HttpStaticFileServer {
	public HttpStaticFileServer(int port) {
		//Configure the server
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		//Set up the event pipeline factory
		bootstrap.setPipelineFactory(new HttpStaticFileServerPipelineFactory());
		
		//Bind and start to accept incoming connections
		bootstrap.bind(new InetSocketAddress(port));
	}

}
