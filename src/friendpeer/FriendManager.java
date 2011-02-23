package friendpeer;

import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.HashMap;

import security.SecureUserTable;
import dht.ValueEvent;
import dht.ValueListener;

public class FriendManager {
	protected HashMap<PublicKey, Friend> KeyToFriend;
	protected HashMap<String, Friend> AliasToKey;
	protected SecureUserTable secureTable;
	public FriendManager() {
		KeyToFriend = new HashMap<PublicKey, Friend>();
		AliasToKey = new HashMap<String, Friend>();
		secureTable = new SecureUserTable();
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
	
	public void findFriend(final String alias) {
		secureTable.get(AliasToKey.get(alias).getPubKey(), new ValueListener<byte[]>(){
			public void valueComplete(final ValueEvent<byte[]> val){
				if(val.exists()){
					AliasToKey.get(alias).setIPaddress(convert(val));
				}
				//TO DO: else do something
			}
		});
	}
	//TO DO: NEED TO IMPLEMENT
	public InetSocketAddress convert(ValueEvent<byte[]> val){
		return null;
	}

}
