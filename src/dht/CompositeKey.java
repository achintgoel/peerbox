package dht;

import java.io.Serializable;

public class CompositeKey<A extends Serializable, B extends Serializable> implements Serializable {
	protected A primaryKey;
	protected B secondaryKey;
	
	public CompositeKey(A primaryKey, B secondaryKey) {
		this.primaryKey = primaryKey;
		this.secondaryKey = secondaryKey;
	}
	
	public A getPrimaryKey() {
		return primaryKey;
	}

	public B getSecondaryKey() {
		return secondaryKey;
	}
}
