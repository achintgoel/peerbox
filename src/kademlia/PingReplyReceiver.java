package kademlia;

public interface PingReplyReceiver {
	public void pingSucceeded();
	public void pingFailed();
}
