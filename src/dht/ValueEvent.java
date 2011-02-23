package dht;


public class ValueEvent<V> {
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
