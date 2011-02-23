package FriendPeer;

import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.HashMap;

public class FriendManager {
	protected HashMap<PublicKey, Friend> KeyToFriend;
	protected HashMap<String, Friend> AliasToKey;
	public FriendManager() {
		KeyToFriend = new HashMap<PublicKey, Friend>();
		AliasToKey = new HashMap<String, Friend>();
	}
	
	public Friend getPublicKey(String alias) {
		return AliasToKey.get(alias);
	}
	
	public Friend getFriend(PublicKey key) {
		return KeyToFriend.get(key);
	}
	
	public void createFriend(String alias, InetSocketAddress IPaddress, PublicKey key) {
		Friend friend = new Friend(this, alias, IPaddress, key);
		KeyToFriend.put(key, friend);
		AliasToKey.put(alias, friend);
	}
	
	void updateFriend(String oldAlias, String newAlias, Friend friend) {
		AliasToKey.remove(oldAlias);
		AliasToKey.put(newAlias, friend);
	}

}
