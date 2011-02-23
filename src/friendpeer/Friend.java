package friendpeer;

import java.net.InetSocketAddress;
import java.security.PublicKey;

public class Friend {
	protected String alias;
	protected InetSocketAddress IPaddress;
	final protected PublicKey pubKey;
	final protected FriendManager manager;
	
	Friend(FriendManager fm, String al, InetSocketAddress IP, PublicKey pub) {
		manager = fm;
		alias = al;
		IPaddress = IP;
		pubKey = pub;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		String oldAlias = this.alias;
		this.alias = alias;
		manager.updateFriend(oldAlias, this.alias, this);
	}

	public InetSocketAddress getIPaddress() {
		return IPaddress;
	}

	public void setIPaddress(InetSocketAddress iPaddress) {
		
		IPaddress = iPaddress;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}


	public FriendManager getManager() {
		return manager;
	}

	
}
