package network;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

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
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
