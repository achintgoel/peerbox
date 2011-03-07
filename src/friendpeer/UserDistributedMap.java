package friendpeer;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;

import security.SecureMessageHandler;
import security.SignedMessage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dht.DistributedMap;
import dht.ValueEvent;
import dht.ValueListener;


public class UserDistributedMap implements DistributedMap<PublicKey, URI>{
	
	DistributedMap<String, String> myDistributedMap;
	SecureMessageHandler secure;

	public UserDistributedMap(DistributedMap<String, String> dm, SecureMessageHandler secure) {
		myDistributedMap = dm;
		//this.secure = secure;
		this.secure = secure;
	}

	@Override
	public void get(final PublicKey key, final ValueListener<URI> vl) {
		
		final Gson gson = new Gson();
		myDistributedMap.get(gson.toJson(key.getEncoded()), new ValueListener<String>(){
			public void valueComplete(final ValueEvent<String> valueEvent) {
				if(valueEvent.exists()) {
					URI address;
					try {
						SignedMessage signedMessage = gson.fromJson(valueEvent.getValue(), SignedMessage.class);
						//System.out.println("Validating signed message");
						//System.out.println("public key: " + key.getEncoded());
						//System.out.println("message: " + signedMessage.getMessage());
						//System.out.println("signature: " + signedMessage.getSignature());
						if(secure.verifyMessage(signedMessage.getMessage(), signedMessage.getSignature(), key)) {
							address = new URI(signedMessage.getMessage());
							vl.valueComplete(new ValueEvent<URI>(address));
						}
						else {
							System.out.println("verification didnt work");
							vl.valueComplete(new ValueEvent<URI>());
						}
					} catch (JsonSyntaxException e) {
						System.out.println("json syntax exception!!");
						e.printStackTrace();
						vl.valueComplete(new ValueEvent<URI>());
						

					} catch (URISyntaxException e) {
						System.out.println("URI syntax exception");
						e.printStackTrace();
						vl.valueComplete(new ValueEvent<URI>());
						
					}
				}
				else {
					vl.valueComplete(new ValueEvent<URI>());
				}
			}
			
		});
		
	}

	@Override
	public void put(PublicKey key, URI val) {
		Gson gson = new Gson();
		SignedMessage signedMessage = new SignedMessage(val.toString(), secure.signMessage(val.toString()));
		myDistributedMap.put(gson.toJson(key.getEncoded()), gson.toJson(signedMessage));
	}


}
