package fileshare;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;

public class FileshareServer extends SimpleChannelUpstreamHandler{
	protected FileShareManager manager;
	
	public FileshareServer(FileShareManager manager) {
		super();
		this.manager = manager;
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		if(request.getMethod() != GET) {
			sendError(ctx, METHOD_NOT_ALLOWED);
			return;
		}
		
		final String path = manager.getFilePath(new URI(request.getUri()));
		if(path == null) {
			sendError(ctx, FORBIDDEN);
			return;
		}
		
		File file = new File(path);
		if (file.isHidden() || !file.exists()) {
			sendError(ctx, NOT_FOUND);
			return;
		}
		if(!file.isFile()) {
			sendError(ctx, FORBIDDEN);
			return;
		}
		
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(file, "r");
			
		} catch (FileNotFoundException fnfe) {
			sendError(ctx, NOT_FOUND);
			return;
		}
		long fileLength = raf.length();
		
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		setContentLength(response, fileLength);
		
		Channel ch = e.getChannel();
		
		//Write the initial line and the header
		ch.write(response);
		
		//Write the content
		ChannelFuture writeFuture;
		if(ch.getPipeline().get(SslHandler.class) != null){
			writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
		} else {
			// No encryption - use zero-copy
			final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
			writeFuture = ch.write(region);
			writeFuture.addListener(new ChannelFutureProgressListener() {
				public void operationComplete(ChannelFuture future) {
					region.releaseExternalResources();
				}
				
				public void operationProgressed(ChannelFuture future, long amount, long current, long total) {
					System.out.printf("%s: %d / %d (+%d)%n", path, current, total, amount);
				}
			});
		}
		
		//Decide whether to close the connection or not.
		if(!isKeepAlive(request)) {
			//close the connection when the whole content is written out
			writeFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Channel ch = e.getChannel();
		Throwable cause = e.getCause();
		if (cause instanceof TooLongFrameException) {
			sendError(ctx, BAD_REQUEST);
			return;
		}
		cause.printStackTrace();
		if(ch.isConnected()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}
	
	//TODO: using URI, figure out actual file path (in our case, figure our the file path from the request id at the end)
	private String sanitizeUri(String uri) {
		return null;
	}
	
	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		         HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
		         response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		         response.setContent(ChannelBuffers.copiedBuffer(
		                 "Failure: " + status.toString() + "\r\n",
		                 CharsetUtil.UTF_8));
		 
		         // Close the connection as soon as the error message is sent.
		         ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
		     }
}
