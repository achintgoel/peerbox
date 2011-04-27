package org.peerbox.friend;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.ValueEvent;
import org.peerbox.dht.ValueListener;
import org.peerbox.kademlia.Value;
import org.peerbox.security.SecureMessageHandler;
import org.peerbox.security.SignedMessage;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;



public class UserDistributedMap implements DistributedMap<PublicKey, URI>{
	
	DistributedMap<String, Value> myDistributedMap;
	SecureMessageHandler secure;

	public UserDistributedMap(DistributedMap<String, Value> dm, SecureMessageHandler secure) {
		myDistributedMap = dm;
		//this.secure = secure;
		this.secure = secure;
	}

	@Override
	public void get(final PublicKey key, final ValueListener<List<URI>> vl) {
		
		final Gson gson = new Gson();
		myDistributedMap.get(gson.toJson(key.getEncoded()), new ValueListener<List<Value>>(){
			public void valueComplete(final ValueEvent<List<Value>> valueEvent) {
				if(valueEvent.exists() && !valueEvent.getValue().isEmpty()) {
					Value newestValue = valueEvent.getValue().get(0);
					for(Value value:valueEvent.getValue()){
						if(newestValue.getTimestamp().before(value.getTimestamp())){
							newestValue = value;
						}
					}
					URI address;
					try {
						SignedMessage signedMessage = gson.fromJson(newestValue.getValue(), SignedMessage.class);
						//System.out.println("Validating signed message");
						//System.out.println("public key: " + key.getEncoded());
						//System.out.println("message: " + signedMessage.getMessage());
						//System.out.println("signature: " + signedMessage.getSignature());
						if(secure.verifyMessage(signedMessage.getMessage(), signedMessage.getSignature(), key)) {
							address = new URI(signedMessage.getMessage());
							List<URI> uriList = new LinkedList<URI>();
							uriList.add(address);
							vl.valueComplete(new ValueEvent<List<URI>>(uriList));
						}
						else {
							System.out.println("verification didnt work");
							vl.valueComplete(new ValueEvent<List<URI>>());
						}
					} catch (JsonSyntaxException e) {
						System.out.println("json syntax exception!!");
						e.printStackTrace();
						vl.valueComplete(new ValueEvent<List<URI>>());
						

					} catch (URISyntaxException e) {
						System.out.println("URI syntax exception");
						e.printStackTrace();
						vl.valueComplete(new ValueEvent<List<URI>>());
						
					}
				}
				else {
					vl.valueComplete(new ValueEvent<List<URI>>());
				}
			}
			
		});
		
	}

	@Override
	public void put(PublicKey key, URI val) {
		Gson gson = new Gson();
		SignedMessage signedMessage = new SignedMessage(val.toString(), secure.signMessage(val.toString()));
		myDistributedMap.put(gson.toJson(key.getEncoded()), new Value(gson.toJson(signedMessage)));
	}

}
