package dht;

import java.io.Serializable;

public interface ValueListener<V extends Serializable> {
	public void valueComplete(ValueEvent<V> valueEvent);
}