package org.peerbox.dht;

public class Value {
	private long publicationTime;
	private String value;

	public Value(String value) {
		this.value = value;
		this.publicationTime = System.currentTimeMillis();
	}

	protected Value() {

	}

	public Value(String value, long date) {
		this.value = value;
		this.publicationTime = date;
	}

	public String getValue() {
		return value;
	}

	public long getPublicationTime() {
		return publicationTime;
	}

}
