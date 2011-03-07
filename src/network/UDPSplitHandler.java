package network;

import java.util.LinkedList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

/**
 * TODO: Implement UDPSplitHandler
 * Netty In/Out Handler that splits/regroups messages into/from multiple UDP packets due
 * to maximum packet length limitations
 * 
 * Uses custom fixed-length header:
 *    First Packet in Message: | 6bit MSG ID | 0 | 9bit LENGTH  |
 *    Subsequent Packets:      | 6bit MSG ID | 1 | 9bit SEQ NUM |
 * 
 *    All numbers are assumed to be Big-Endian positive binary
 *    
 *    MSG IDs are unique for a specific message, every 64 messages sent:
 *    Range (0, 63)
 *    
 *    LENGTH specifies the number of packets to be sent as part of the 
 *    message (0 means only this packet)
 *    
 *    SEQ NUM specifies (in ascending numerical order) the position of
 *    the specific packet in the message sequence
 *    (1 means the second packet in the sequence, no sequence 
 *    number is present for the 0th packet)
 *    Max = LENGTH
 * 
 * @author rajiv
 */
public class UDPSplitHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {
	
	private final PacketMessageTable pmt;
	
	public UDPSplitHandler() {
		super();
		pmt = new PacketMessageTable(63, 511);
	}
	
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }
		Object m = ((MessageEvent) e).getMessage();
		if (!(m instanceof ChannelBuffer)) {
            ctx.sendDownstream(e);
            return;
        }
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (!(e instanceof MessageEvent)) {
            ctx.sendUpstream(e);
            return;
        }
		Object m = ((MessageEvent) e).getMessage();
		if (!(m instanceof ChannelBuffer)) {
            ctx.sendUpstream(e);
            return;
        }
		ChannelBuffer cb = (ChannelBuffer) m;
		
		byte[] idBytes = new byte[2];
		cb.getBytes(0, idBytes);
		byte[] seqBytes = new byte[1];
		cb.getBytes(2, seqBytes);

		int seq = 0;
		seq |= seqBytes[0];
		seq <<= 8;
		seq |= seqBytes[1];
		
		
	}
	
	
}
