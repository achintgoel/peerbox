package dht;


public class CompositeKey<A, B> {
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
