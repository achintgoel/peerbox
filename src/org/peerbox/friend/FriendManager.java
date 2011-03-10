package org.peerbox.friend;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.ValueEvent;
import org.peerbox.dht.ValueListener;
import org.peerbox.security.SecureMessageHandler;


public class FriendManager {
	protected HashMap<PublicKey, Friend> keyToFriendMap;
	protected HashMap<String, Friend> aliasToFriendMap;
	protected UserDistributedMap userTable;
	protected SecureMessageHandler secure;
	protected PublicKey myPubKey;
	protected PrivateKey myPrivKey;
	protected URI myURI;
	
	public FriendManager(DistributedMap<String, String> map, SecureMessageHandler secure, URI myURI) {
		keyToFriendMap = new HashMap<PublicKey, Friend>();
		aliasToFriendMap = new HashMap<String, Friend>();
		
		this.secure = secure;
		this.myURI = myURI;
		KeyPair keypair = secure.getKeyPair();
		myPubKey = keypair.getPublic();
		myPrivKey = keypair.getPrivate();
		userTable = new UserDistributedMap(map, secure);
	}
	
	
	
	public Friend getFriend(String alias) {
		return aliasToFriendMap.get(alias);
	}
	
	public Friend getFriend(PublicKey key) {
		return keyToFriendMap.get(key);
	}
	
	public void signOn(){
		userTable.put(myPubKey, myURI);
		
		System.out.println("Public Key: "+ Base64.encodeBase64URLSafeString(myPubKey.getEncoded()));		
	}
	
	public void createFriend(String alias, URI address, PublicKey key) {
		Friend friend = new Friend(this, alias, address, key);
		keyToFriendMap.put(key, friend);
		aliasToFriendMap.put(alias, friend);
		updateFriendURI(friend);
//		System.out.println("This friends uri is "+friend.getNetworkAddress().toString());
		
	}
	public void removeFriend(String alias) {
		if(aliasToFriendMap.get(alias) != null) {
			keyToFriendMap.remove(aliasToFriendMap.get(alias).getPubKey());
			aliasToFriendMap.remove(alias);
		}
	}
	
	void updateFriend(String oldAlias, String newAlias, Friend friend) {
		aliasToFriendMap.remove(oldAlias);
		aliasToFriendMap.put(newAlias, friend);
	}
	
	public void updateFriendURI(final Friend friend) {
		userTable.get(friend.getPubKey(), new ValueListener<URI>(){
			public void valueComplete(final ValueEvent<URI> val){
				if(val.exists()){
					friend.setAddress(val.getValue());
					System.out.println("Found address of friend: "+friend.getNetworkAddress().toString());
				}
				else {
					System.out.println("Unable to find address of friend");
				}
			}
		});
	}
	
	public void printBuddyList(){
		//TODO: Print the buddys nicely
		if (keyToFriendMap.isEmpty()) {
			System.out.println("No friends added yet. Stop being such a loner.");
		} else {
			int i = 1;
			System.out.println("Your friends:");
			for(Entry<PublicKey, Friend> buddyInfo : keyToFriendMap.entrySet()) {
				String uri;
				if (buddyInfo.getValue().isAlive()) {
					uri = buddyInfo.getValue().getNetworkAddress().toString();
				} else {
					uri = "offline";
				}
				System.out.printf("\t%d. %s (%s)", i, buddyInfo.getValue().getAlias(), uri);
				i++;
			}
		}	
	}

	public Collection<Friend> getAllFriends() {
		return keyToFriendMap.values();
	}
}
