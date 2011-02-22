package kademlia;

public class Configuration {
	final static int alpha = 3;
	final static int B = 160;
	final static int k = 20;
	final static int MIN_PING_INTERVAL_SECS = 60;
	
	public int getK() {
		return k;
	}
	
	public int getB() {
		return B;
	}
}
