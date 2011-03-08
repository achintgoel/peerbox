package org.peerbox.network.http;
import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.peerbox.fileshare.FileShareManager;
import org.peerbox.fileshare.FileshareServer;


public class HttpStaticFileServerPipelineFactory implements ChannelPipelineFactory{
	protected FileShareManager manager;
	
	public HttpStaticFileServerPipelineFactory(FileShareManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("handler", new FileshareServer(manager));
		return pipeline;
	}
	

}
