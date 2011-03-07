package network;

import java.math.BigInteger;
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
	LinkedHashMap<Short, PacketSequence> messages;
	int lastMessageId;
	int maxConcurrentMessages;
	int maxSequenceId;
	int timeout;
	
	public PacketMessageTable(int maxConcurrentMessages, int maxSequenceId, int timeout){
		lastMessageId = 0;
		messages = new LinkedHashMap<Short, PacketSequence>();
		this.maxConcurrentMessages = maxConcurrentMessages;
		this.maxSequenceId = maxSequenceId;
		this.timeout = timeout;
	}
	
	public ChannelBuffer put(short messageId, int sequenceId, ChannelBuffer channelBuffer){
		PacketSequence currentMessage = getCurrentSequence(messageId);
		currentMessage.put(sequenceId, channelBuffer);
		System.out.println("currentMessageCount" + currentMessage.getCount());
		if(currentMessage.getCount() == 0){
			ChannelBuffer[] channelArray = currentMessage.toArray();
			int length = messages.get(messageId).getLength();
			messages.remove(messageId);
			if(length > 1){
				return ChannelBuffers.wrappedBuffer(channelArray);
			}
			else{
				return channelArray[0];
			}
		}
		else{
			return null;
		}
	}
	
	public void setLength(short messageId, int length){
		System.out.println("length set to " + length);
		PacketSequence currentSequence = getCurrentSequence(messageId);
		currentSequence.setLength(length);
	}
	
	public int getNumMessages(){
		return messages.size();
	}	
	
	protected PacketSequence getCurrentSequence(short messageId){		
		PacketSequence currentSequence;
		if(messages.containsKey(messageId)){
			currentSequence = messages.get(messageId);
			//messages.remove(messageId);
			//messages.put(messageId, currentSequence);
		}
		else{
			if(messages.size() == maxConcurrentMessages){
				Iterator<Entry<Short, PacketSequence>> iterator = messages.entrySet().iterator();
				Entry<Short, PacketSequence> entry = iterator.next();
				messages.remove(entry.getKey());
				iterator = messages.entrySet().iterator();
				while(iterator.hasNext()){
					entry = iterator.next();
					long timestamp = entry.getValue().getTimeStamp();
					if(timestamp + timeout < System.currentTimeMillis()/1000){
						messages.remove(entry.getKey());
						iterator = messages.entrySet().iterator();
					}
					else{
						break;
					}
				}
			}
			currentSequence = new PacketSequence(maxSequenceId);
			messages.put(messageId, currentSequence);
		}
		return currentSequence;
	}
}
