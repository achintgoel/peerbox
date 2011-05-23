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
	final static int minExpiry = 10;
	final static int maxExpiry = 40;
	final static int replicateInterval = 30;
	final static int republishInterval = 90;
	
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

	public int getMinExpiry() {
		return minExpiry;
	}

	public int getMaxExpiry() {
		return maxExpiry;
	}
	
	public int getReplicateInterval() {
		return replicateInterval;
	}
	
	public int getRepublishInterval() {
		return republishInterval;
	}
}
