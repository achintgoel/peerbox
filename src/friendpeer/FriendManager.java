package friendpeer;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map.Entry;

import security.SecureMessageHandler;
import dht.ValueEvent;
import dht.ValueListener;

public class FriendManager {
	protected HashMap<PublicKey, Friend> KeyToFriend;
	protected HashMap<String, Friend> AliasToKey;
	protected UserDistributedMap userTable;
	protected SecureMessageHandler secure;
	protected PublicKey myPubKey;
	protected PrivateKey myPrivKey;
	public FriendManager() {
		KeyToFriend = new HashMap<PublicKey, Friend>();
		AliasToKey = new HashMap<String, Friend>();
		userTable = null;
		secure = new SecureMessageHandler();
		KeyPair keypair = secure.generateKeyPairs();
		myPubKey = keypair.getPublic();
		myPrivKey = keypair.getPrivate();
		
	}
	
	public Friend getPublicKey(String alias) {
		return AliasToKey.get(alias);
	}
	
	public Friend getFriend(PublicKey key) {
		return KeyToFriend.get(key);
	}
	
	public void signOn(int port){
		try {
			//TODO: change the localhost part
			userTable.put(myPubKey, new URI("udp://localhost:"+port));
			System.out.println(new BigInteger(myPubKey.getEncoded()));

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void createFriend(String alias, URI address, PublicKey key) {
		Friend friend = new Friend(this, alias, address, key);
		updateFriendURI(friend);
		System.out.println("This friends uri is "+friend.getNetworkAddress().toString());
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
	
	public void printBuddyList(){
		for(Entry<PublicKey, Friend> buddyInfo : KeyToFriend.entrySet()) {
			System.out.println("alias: "+buddyInfo.getValue().getAlias()+" URI: "+buddyInfo.getValue().getNetworkAddress()+" Public Key:"+buddyInfo.getKey()+"/n");
		}
	}

}
