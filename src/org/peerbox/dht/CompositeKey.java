package org.peerbox.dht;

public class CompositeKey<A, B> {
	protected A primaryKey;
	protected B secondaryKey;

	protected CompositeKey() {

	}

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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (this.getClass() != o.getClass()) {
			return false;
		}

		CompositeKey<?, ?> ck = (CompositeKey<?, ?>) o;

		return primaryKey.equals(ck.primaryKey) && secondaryKey.equals(ck.secondaryKey);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + primaryKey.hashCode();
		result = 31 * result + secondaryKey.hashCode();
		return result;
	}
}
