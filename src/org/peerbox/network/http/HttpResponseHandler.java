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

public class HttpResponseHandler extends SimpleChannelUpstreamHandler {

	private boolean saveChunks;
	protected RandomAccessFile downloadFile;
	protected HttpClientListener listener;

	public HttpResponseHandler(File downloadFilePath, HttpClientListener listener) {
		super();
		this.listener = listener;
		try {
			downloadFile = new RandomAccessFile(new File(downloadFilePath.getAbsolutePath()), "rw");
		} catch (FileNotFoundException e) {
			listener.localFileError();
		}
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (!saveChunks) {
			if (!(e.getMessage() instanceof HttpResponse)) {
				return;
			}
			HttpResponse response = (HttpResponse) e.getMessage();

			// System.out.println("STATUS: " + response.getStatus());
			// System.out.println("VERSION: " + response.getProtocolVersion());
			// System.out.println();

			if (!response.getHeaderNames().isEmpty()) {
				for (String name : response.getHeaderNames()) {
					for (String value : response.getHeaders(name)) {
						// System.out.println("HEADER: " + name + " = " +
						// value);
					}
				}
				// System.out.println();
			}

			if (response.getStatus().getCode() == 200) {
				listener.started();
				if (!response.isChunked()) {
					ChannelBuffer content = response.getContent();
					if (content.readable()) {
						downloadFile.write(content.array());
						downloadFile.close();
						listener.finished();
					}
				} else {
					saveChunks = true;
				}

				/*
				 * System.out.println("CONTENT {");
				 * System.out.println(content.toString(CharsetUtil.UTF_8));
				 * System.out.println("} END OF CONTENT");
				 */

			} else {
				listener.downloadError();
			}
		} else {
			// TODO: save content to file on file system
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				saveChunks = false;
				downloadFile.close();
				listener.finished();
			} else {
				downloadFile.write(chunk.getContent().array());
				// System.out.print(chunk.getContent().toString(CharsetUtil.UTF_8));
				// System.out.flush();
			}
		}
	}

}
