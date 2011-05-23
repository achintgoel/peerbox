package org.peerbox.network.http;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class HttpClient {
	public HttpClient(URI uri, File downloadFilePath, HttpClientListener listener) {

		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "localhost" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			port = 80;
		}
		// Configure the client
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory(false, new HttpResponseHandler(downloadFilePath,
				listener)));

		// Start the connection attempt
		System.out.println("trying to connect to host:" + uri.toString());
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		Channel channel = future.awaitUninterruptibly().getChannel();

		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}

		// Prepare the HTTP request
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
		request.setHeader(HttpHeaders.Names.HOST, host);
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

		// Send the HTTP request
		channel.write(request);

		// Wait for the server to close the connection
		channel.getCloseFuture().awaitUninterruptibly();

		// Shut down executor threads to exit
		bootstrap.releaseExternalResources();

	}
}
