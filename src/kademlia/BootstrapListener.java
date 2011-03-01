package kademlia;

public interface BootstrapListener {
	void onBootstrapSuccess();
	void onBootstrapFailure();
}
