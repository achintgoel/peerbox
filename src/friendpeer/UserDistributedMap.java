package friendpeer;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivateKey;
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
	PrivateKey myKey;

	public UserDistributedMap(DistributedMap<String, String> dm, PrivateKey priv) {
		myDistributedMap = dm;
		secure = new SecureMessageHandler();
		priv = myKey;
	}

	@Override
	public void get(final PublicKey key, final ValueListener<URI> vl) {
		
		final Gson gson = new Gson();
		myDistributedMap.get(gson.toJson(key.getEncoded()), new ValueListener<String>(){
			public void valueComplete(final ValueEvent<String> valueEvent) {
				if(valueEvent.exists()) {
					URI address;
					try {
						String value = gson.fromJson(valueEvent.getValue(), SignedMessage.class).getMessage();
						System.out.println("value is "+value);
						if(secure.verifyMessage(value, gson.fromJson(valueEvent.getValue(), SignedMessage.class).getSignature(), key)) {
							address = new URI(value);
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
		SignedMessage sig = new SignedMessage(val.toString(), secure.signMessage(val.toString(), myKey));
		myDistributedMap.put(gson.toJson(key.getEncoded()), gson.toJson(sig));
	}


}
