package network.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class HttpResponseHandler extends SimpleChannelUpstreamHandler{
	
	private boolean readingChunks;
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(!readingChunks) {
			
		}
		else {
			//TODO: save content to file on file system
		}
	}
	

}
