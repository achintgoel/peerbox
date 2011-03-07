package network;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;

public class PacketMessageTable {
	PacketSequence[] messages;
	int lastMessageId;
	int maxMessageId;
	
	public PacketMessageTable(int maxMessageId, int maxSequenceId){
		lastMessageId = 0;
		messages = new PacketSequence[maxMessageId];
		for(int i = 0; i < maxMessageId; i++){
			messages[i] = new PacketSequence(maxSequenceId);
		}
		this.maxMessageId = maxMessageId;
	}
	
	public ChannelBuffer put(int messageId, int sequenceId, ChannelBuffer channelBuffer){
		PacketSequence currentMessage = messages[messageId];
		currentMessage.put(sequenceId, channelBuffer);
		if(messageId > lastMessageId){
			lastMessageId = messageId;
			int toClear = lastMessageId - maxMessageId/2;
			messages[toClear].clear();
		}
		if(currentMessage.count == 0){
			ArrayList<ChannelBuffer> channelList = new ArrayList<ChannelBuffer>();
			channelList = currentMessage.toArrayList();
			//TODO: merge the first length packets
			currentMessage.clear();
			// TODO: return the new ChannelBuffer
			return null;
		}
		else{
			return null;
		}
	}
	
	public void setLength(int messageId, int length){
		messages[messageId].setLength(length);
	}
	
}
