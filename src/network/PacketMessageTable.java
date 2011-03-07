package network;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketMessageTable {
	protected ArrayList<PacketSequence> messages;
	int lastMessageId;
	int maxMessageId;
	
	public PacketMessageTable(int maxMessageId, int maxSequenceId){
		lastMessageId = 0;
		messages = new ArrayList<PacketSequence>(maxMessageId);
		for(int i = 0; i < maxMessageId; i++){
			messages.add(i, new PacketSequence(maxSequenceId));
		}
		this.maxMessageId = maxMessageId;
	}
	
	public ArrayList<ChannelBuffer> put(int messageId, int sequenceId, ChannelBuffer channelBuffer){
		return null;
	}
	
	public void setLength(int messageID, int length){
		
	}
	
}
