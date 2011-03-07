package network;

import java.util.LinkedHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class PacketMessageTable {
	LinkedHashMap<byte[], PacketSequence> messages;
	int lastMessageId;
	int maxConcurrentMessages;
	int maxSequenceId;
	
	public PacketMessageTable(int maxConcurrentMessages, int maxSequenceId){
		lastMessageId = 0;
		messages = new LinkedHashMap<byte[], PacketSequence>();
		this.maxConcurrentMessages = maxConcurrentMessages;
		this.maxSequenceId = maxSequenceId;
	}
	
	public ChannelBuffer put(byte[] messageId, int sequenceId, ChannelBuffer channelBuffer){
		PacketSequence currentMessage;
		if(!messages.containsKey(messageId)){
			if(messages.size() == maxConcurrentMessages){
				byte[] firstId = messages.keySet().iterator().next();
				messages.remove(firstId);
			}
			currentMessage = new PacketSequence(maxSequenceId);
			messages.put(messageId, currentMessage);
		}
		else{
			currentMessage = messages.get(messageId);
		}
		currentMessage.put(sequenceId, channelBuffer);
		if(currentMessage.getCount() == 0){
			ChannelBuffer[] channelArray = currentMessage.toArray();
			messages.remove(messageId);
			return ChannelBuffers.wrappedBuffer(channelArray);
		}
		else{
			return null;
		}
	}
	
	public void setLength(byte[] messageId, int length){
		PacketSequence currentSequence = messages.get(messageId);
		currentSequence.setLength(length);
	}
	
}
