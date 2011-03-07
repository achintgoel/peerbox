package network;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;


/**
 * This class stores the packets from one message sorted according to the sequence numbers
 * @author vineet
 *
 */
public class PacketSequence {
	ChannelBuffer[] packets;
	int count;
	int maxSequenceId;
	int length;
	long timestamp;
	
	public PacketSequence(int maxSequenceId){
		packets = new ChannelBuffer[maxSequenceId + 1];
		this.maxSequenceId = maxSequenceId;
		this.count = 0;
		this.length = 0;
	}
	
	
	public void put(int sequenceId, ChannelBuffer channelBuffer){
		timestamp = System.currentTimeMillis()/1000;
		packets[sequenceId] = channelBuffer;
		count++;
	}
	
	public void setLength(int length){
		count = count - length + 1;
		this.length = length; 
	}
	
	public int getLength(){
		return length;
	}
	
	public int getCount(){
		return count;
	}

	public ChannelBuffer[] toArray() {
		ChannelBuffer[] returnArray = new ChannelBuffer[length];
		for(int i = 0; i < length; i++){
			returnArray[i] = packets[i];
		}
		return returnArray;
	}
	
	public long getTimeStamp(){
		return timestamp;
	}
}
