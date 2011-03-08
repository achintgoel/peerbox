package org.peerbox.friendpeer;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map.Entry;

import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.ValueEvent;
import org.peerbox.dht.ValueListener;
import org.peerbox.security.SecureMessageHandler;


public class FriendManager {
	protected HashMap<PublicKey, Friend> KeyToFriend;
	protected HashMap<String, Friend> AliasToKey;
	protected UserDistributedMap userTable;
	protected SecureMessageHandler secure;
	protected PublicKey myPubKey;
	protected PrivateKey myPrivKey;
	
	public FriendManager(DistributedMap<String, String> map, SecureMessageHandler secure) {
		KeyToFriend = new HashMap<PublicKey, Friend>();
		AliasToKey = new HashMap<String, Friend>();
		
		this.secure = secure;
		KeyPair keypair = secure.getKeyPair();
		myPubKey = keypair.getPublic();
		myPrivKey = keypair.getPrivate();
		userTable = new UserDistributedMap(map, secure);
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
			System.out.println("Public Key: "+new BigInteger(myPubKey.getEncoded()));
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createFriend(String alias, URI address, PublicKey key) {
		Friend friend = new Friend(this, alias, address, key);
		KeyToFriend.put(key, friend);
		AliasToKey.put(alias, friend);
		updateFriendURI(friend);
//		System.out.println("This friends uri is "+friend.getNetworkAddress().toString());
		
	}
	
	void updateFriend(String oldAlias, String newAlias, Friend friend) {
		AliasToKey.remove(oldAlias);
		AliasToKey.put(newAlias, friend);
	}
	
	public void updateFriendURI(final Friend friend) {
		userTable.get(friend.getPubKey(), new ValueListener<URI>(){
			public void valueComplete(final ValueEvent<URI> val){
				if(val.exists()){
					friend.setAddress(val.getValue());
					System.out.println("new URI of friend is: "+friend.getNetworkAddress().toString());
				}
				else {
					System.out.println("New uri retrieval didnt work!!");
				}
					
			//TO DO: else do something
			}
		});
	}
	
	public void printBuddyList(){
		for(Entry<PublicKey, Friend> buddyInfo : KeyToFriend.entrySet()) {
			System.out.println("alias: "+buddyInfo.getValue().getAlias()+" URI: "+buddyInfo.getValue().getNetworkAddress()+" Public Key:"+new BigInteger(buddyInfo.getKey().getEncoded())+"/n");
		}
	}

}
