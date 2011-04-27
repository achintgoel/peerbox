package org.peerbox.kademlia;


/**
 * The configuration of the networkInstance
 * holds the value of alpha, k and other constants
 * 
 *
 */
public class Configuration {
	final static int alpha = 3;
	final static int B = 160;
	final static int k = 20;
	final static int MIN_PING_INTERVAL_SECS = 60;
	final static int refreshInterval = 900;
	final static int expiryInterval = 60;
	
	public int getK() {
		return k;
	}
	
	public int getB() {
		return B;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	public int getRefreshInterval(){
		return refreshInterval;
	}

	public int getExpiryInterval() {
		return expiryInterval;
	}
}
