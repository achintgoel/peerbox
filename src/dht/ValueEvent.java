package dht;

import java.io.Serializable;

public class ValueEvent<V extends Serializable> {
	protected V value;
	protected boolean exists;
	
	public ValueEvent(V value) {
		this.value = value;
		this.exists = true;
	}
	
	public ValueEvent() {
		this.value = null;
		this.exists = false;
	}
	
	public V getValue() {
		return this.value;
	}
	
	public boolean exists() {
		return this.exists;
	}
}
