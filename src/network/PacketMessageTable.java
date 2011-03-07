package network;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;


/**
 * This class stores the messages that have been partially received
 * @author vineet
 *
 */
public class PacketMessageTable {
	LinkedHashMap<byte[], PacketSequence> messages;
	int lastMessageId;
	int maxConcurrentMessages;
	int maxSequenceId;
	int timeout;
	
	public PacketMessageTable(int maxConcurrentMessages, int maxSequenceId, int timeout){
		lastMessageId = 0;
		messages = new LinkedHashMap<byte[], PacketSequence>();
		this.maxConcurrentMessages = maxConcurrentMessages;
		this.maxSequenceId = maxSequenceId;
		this.timeout = timeout;
	}
	
	public ChannelBuffer put(byte[] messageId, int sequenceId, ChannelBuffer channelBuffer){
		PacketSequence currentMessage;
		if(!messages.containsKey(messageId)){
			if(messages.size() == maxConcurrentMessages){
				Iterator<Entry<byte[], PacketSequence>> iterator = messages.entrySet().iterator();
				Entry<byte[], PacketSequence> entry = iterator.next();
				byte[] nextId = entry.getKey();
				PacketSequence nextMessage = entry.getValue();
				long timestamp = nextMessage.getTimeStamp();
				messages.remove(nextId);
				while(iterator.hasNext() && (timestamp + timeout) < System.currentTimeMillis()/1000){
					entry = iterator.next();					
				}
			}
			currentMessage = new PacketSequence(maxSequenceId);
			messages.put(messageId, currentMessage);
		}
		else{
			currentMessage = messages.get(messageId);
			messages.remove(messageId);
			messages.put(messageId, currentMessage);
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
