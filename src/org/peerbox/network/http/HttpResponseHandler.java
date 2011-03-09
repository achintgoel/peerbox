package org.peerbox.network.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

public class HttpResponseHandler extends SimpleChannelUpstreamHandler{
	
	private boolean readingChunks;
	protected RandomAccessFile downloadFile;
	
	public HttpResponseHandler(String filename) {
		super();
		try {
			downloadFile = new RandomAccessFile(new File("/home/rajiv/Downloads/"+filename), "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("RANDOM ACCESS FILE DID NOT INITIALIZE PROPERLY!!!");
			e.printStackTrace();
		}
	}
	
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(!readingChunks) {
			HttpResponse response = (HttpResponse) e.getMessage();
			  
			              System.out.println("STATUS: " + response.getStatus());
			              System.out.println("VERSION: " + response.getProtocolVersion());
			              System.out.println();
			  
			              if (!response.getHeaderNames().isEmpty()) {
			                  for (String name: response.getHeaderNames()) {
			                	  for (String value: response.getHeaders(name)) {
			                          System.out.println("HEADER: " + name + " = " + value);
			                      }
			                  }
			                  System.out.println();
			              }
			  
			              if (response.getStatus().getCode() == 200 && response.isChunked()) {
			                  readingChunks = true;
			                  System.out.println("STARTING TO RECEIVE CHUNKED CONTENT");
			              } else {
			                  ChannelBuffer content = response.getContent();
			                  if (content.readable()) {
			                	  downloadFile.write(content.array());
			                	  downloadFile.close();
			                	  /*
			                      System.out.println("CONTENT {");
			                      System.out.println(content.toString(CharsetUtil.UTF_8));
			                      System.out.println("} END OF CONTENT");
			                      */
			                	  
			                  }
			              }
		}
		else {
			//TODO: save content to file on file system
			HttpChunk chunk = (HttpChunk) e.getMessage();
			              if (chunk.isLast()) {
			                  readingChunks = false;
			                  downloadFile.close();
			                  System.out.println("ENDED RECEIVING CHUNKED CONTENT");
			              } else {
			            	  downloadFile.write(chunk.getContent().array());
			                  //System.out.print(chunk.getContent().toString(CharsetUtil.UTF_8));
			                  //System.out.flush();
			              }
		}
	}
	

}
