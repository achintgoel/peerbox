package org.peerbox.network.http;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {
	private final boolean ssl;
	protected HttpResponseHandler responseHandler;

	public HttpClientPipelineFactory(boolean ssl, HttpResponseHandler responseHandler) {
		this.ssl = ssl;
		this.responseHandler = responseHandler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = pipeline();

		pipeline.addLast("codec", new HttpClientCodec());
		pipeline.addLast("inflater", new HttpContentDecompressor());

		pipeline.addLast("handler", responseHandler);
		return pipeline;
		// return null;
	}

}
