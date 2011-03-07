package friendpeer;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;

import security.SignedMessage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dht.DistributedMap;
import dht.ValueEvent;
import dht.ValueListener;


public class UserDistributedMap implements DistributedMap<PublicKey, URI>{
	
	DistributedMap<String, String> myDistributedMap;

	public UserDistributedMap(DistributedMap<String, String> dm) {
		myDistributedMap = dm;
	}

	@Override
	public void get(PublicKey key, final ValueListener<URI> vl) {
		
		final Gson gson = new Gson();
		//TODO: PublicKey cannot be converted to a String via toString!
		myDistributedMap.get(key.toString(), new ValueListener<String>(){
			public void valueComplete(final ValueEvent<String> valueEvent) {
				if(valueEvent.exists()) {
					URI address;
					try {
						//TODO: Validate Signed Message Prior to Returning!
						address = new URI(gson.fromJson(valueEvent.getValue(), SignedMessage.class).getMessage());
						vl.valueComplete(new ValueEvent<URI>(address));
					} catch (JsonSyntaxException e) {
						e.printStackTrace();

					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
				else {
					vl.valueComplete(new ValueEvent<URI>());
				}
			}
			
		});
		
	}

	@Override
	public void put(PublicKey key, URI value) {
		//TODO: This does not work! Must create a signed message of the URI, not serialize URI directly for storage in map!
		//Additionally, public key cannot be stored with "toString" must serialize to binary first, and store as part of a
		//serialized object!
		Gson gson = new Gson();
		myDistributedMap.put(key.toString(), gson.toJson(value));
	}


}
