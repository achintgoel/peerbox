package org.peerbox.kademlia;

import java.util.Date;

public class Value {
	private Date timestamp;
	private String value;
	
	public Value(String value){
		this.value = value;
		this.timestamp = new Date(System.currentTimeMillis());
	}
	
	protected Value(){
		
	}
	
	public Value(String value, Date date){
		this.value = value;
		this.timestamp = date;
	}
	
	public String getValue(){
		return value;
	}
	
	public Date getTimestamp(){
		return timestamp;
	}

}
