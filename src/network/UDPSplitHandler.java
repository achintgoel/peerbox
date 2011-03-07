package network;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;

/**
 * TODO: Implement UDPSplitHandler
 * Netty In/Out Handler that splits/regroups messages into/from multiple UDP packets due
 * to maximum packet length limitations
 * 
 * 
 * 
 * @author rajiv
 */
public class UDPSplitHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {
	
	private final HashMap<SocketAddress, PacketMessageTable> pmtMap;
	private final int maxPacketSize;
	private final int MAX_SEQUENCE_SIZE = 65536;
	private final int FIRST_MESSAGE_HEADER_SIZE = 4;
	private final int NEXT_MESSAGE_HEADER_SIZE = 3;
	private int lastSentMessageId;
	
	public UDPSplitHandler(int maxPacketSize) {
		super();
		pmtMap = new HashMap<SocketAddress, PacketMessageTable>();
		this.maxPacketSize = maxPacketSize;
		lastSentMessageId = new Random().nextInt();
	}
	
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }
		MessageEvent me = (MessageEvent) e; 
		Object m = me.getMessage();
		if (!(m instanceof ChannelBuffer)) {
            ctx.sendDownstream(e);
            return;
        }
		
		ChannelBuffer cb = (ChannelBuffer) m;
		if (!cb.readable()) return;
		
		int totalBytes = cb.readableBytes();
		int totalPackets = 1;
		if (totalBytes + FIRST_MESSAGE_HEADER_SIZE > maxPacketSize) {
			int tbMinusFirst = totalBytes - maxPacketSize + FIRST_MESSAGE_HEADER_SIZE;
			totalPackets += tbMinusFirst / (maxPacketSize - NEXT_MESSAGE_HEADER_SIZE);
		}
		if (totalPackets > MAX_SEQUENCE_SIZE) {
			//TODO: how do we throw a failure?
			return;
		}
		
		byte[] messageId = new byte[2];
		messageId[0] = (byte) lastSentMessageId++;
		messageId[1] = (byte) (lastSentMessageId >>> 8);
		byte[] header = new byte[FIRST_MESSAGE_HEADER_SIZE];
		header[0] = messageId[0];
		header[1] = messageId[1];
		header[2] = 0;
		header[3] = (byte) (totalPackets - Byte.MAX_VALUE);
		ChannelBuffer headerBuf = ChannelBuffers.copiedBuffer(header);
		int dataSize = cb.readableBytes() < (maxPacketSize - FIRST_MESSAGE_HEADER_SIZE) ? cb.readableBytes() : (maxPacketSize - FIRST_MESSAGE_HEADER_SIZE);
		ChannelBuffer dataBuf = cb.slice(cb.readerIndex(), dataSize);
		cb.readerIndex(cb.readerIndex() + dataSize);
		ChannelBuffer frameBuf = ChannelBuffers.wrappedBuffer(headerBuf, dataBuf);
		Channels.write(ctx, null, frameBuf);
		int packet = 0;
		while (cb.readable()) {
			header = new byte[NEXT_MESSAGE_HEADER_SIZE];
			header[0] = messageId[0];
			header[1] = messageId[1];
			header[2] = (byte) (++packet - Byte.MAX_VALUE);
			headerBuf = ChannelBuffers.copiedBuffer(header);
			dataSize = cb.readableBytes() < (maxPacketSize - NEXT_MESSAGE_HEADER_SIZE) ? cb.readableBytes() : (maxPacketSize - NEXT_MESSAGE_HEADER_SIZE);
			dataBuf = cb.slice(cb.readerIndex(), dataSize);
			cb.readerIndex(cb.readerIndex() + dataSize);
			frameBuf = ChannelBuffers.wrappedBuffer(headerBuf, dataBuf);
			Channels.write(ctx, null, frameBuf);
		}
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (!(e instanceof MessageEvent)) {
            ctx.sendUpstream(e);
            return;
        }
		MessageEvent me = (MessageEvent) e; 
		Object m = me.getMessage();
		if (!(m instanceof ChannelBuffer)) {
            ctx.sendUpstream(e);
            return;
        }
		ChannelBuffer cb = (ChannelBuffer) m;
		if (!cb.readable()) return;
		
		try {
			short id = cb.readShort();
			int seq = ((int) cb.readByte()) + Byte.MAX_VALUE;
	
			
			PacketMessageTable pmt = pmtMap.get(me.getRemoteAddress());
			if (pmt == null) {
				pmt = new PacketMessageTable(32, MAX_SEQUENCE_SIZE - 1, 45);
				pmtMap.put(me.getRemoteAddress(), pmt);
			}
						
			if (seq == 0) {
				int length = ((int) cb.readByte()) + Byte.MAX_VALUE;
				pmt.setLength(id, length + 1);
			}
			cb.discardReadBytes();
			cb = pmt.put(id, seq, cb);
			if (cb != null) {
				Channels.fireMessageReceived(ctx, cb, me.getRemoteAddress());
			}
		} catch (IndexOutOfBoundsException exception) {
			exception.printStackTrace();
		}
	}
	
	
}
