package network;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketSequence {
	protected final ArrayList<ChannelBuffer> packets;
	int count;
	int maxSequenceId;
	
	public PacketSequence(int maxSequenceId){
		packets = new ArrayList<ChannelBuffer>(maxSequenceId);
		this.maxSequenceId = maxSequenceId;
		this.count = 0;
	}
	
	public void clear(int messageId){
		count = 0;
		
	}
	
	public void put(int messageId, int sequenceId, ChannelBuffer channelBuffer){
		
	}
	
	public void setLength(int messageId, int length){
	}
}
