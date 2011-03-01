package friendpeer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.PublicKey;
import java.util.HashMap;

import dht.ValueEvent;
import dht.ValueListener;

public class FriendManager {
	protected HashMap<PublicKey, Friend> KeyToFriend;
	protected HashMap<String, Friend> AliasToKey;
	protected UserDistributedMap userTable;
	public FriendManager() {
		KeyToFriend = new HashMap<PublicKey, Friend>();
		AliasToKey = new HashMap<String, Friend>();
		userTable = null;
	}
	
	public Friend getPublicKey(String alias) {
		return AliasToKey.get(alias);
	}
	
	public Friend getFriend(PublicKey key) {
		return KeyToFriend.get(key);
	}
	
	public void createFriend(String alias, URI address, PublicKey key) {
		Friend friend = new Friend(this, alias, address, key);
		KeyToFriend.put(key, friend);
		AliasToKey.put(alias, friend);
	}
	
	void updateFriend(String oldAlias, String newAlias, Friend friend) {
		AliasToKey.remove(oldAlias);
		AliasToKey.put(newAlias, friend);
	}
	
	public void updateFriendURI(final Friend friend) {
		userTable.get(AliasToKey.get(friend).getPubKey(), new ValueListener<URI>(){
			public void valueComplete(final ValueEvent<URI> val){
				if(val.exists()){
					friend.setAddress(val.getValue());
				}
					
			//TO DO: else do something
			}
		});
	}


}
