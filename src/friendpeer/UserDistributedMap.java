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
		myDistributedMap.get(key.toString(), new ValueListener<String>(){
			public void valueComplete(final ValueEvent<String> valueEvent) {
				if(valueEvent.exists()) {
					URI address;
					try {
						address = new URI(gson.fromJson(valueEvent.getValue(), SignedMessage.class).getMessage());
						vl.valueComplete(new ValueEvent<URI>(address));
					} catch (JsonSyntaxException e) {

					} catch (URISyntaxException e) {

					}
					
				}
				else
					vl.valueComplete(new ValueEvent<URI>());
			}
			
		});
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(PublicKey key, URI value) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		myDistributedMap.put(key.toString(), gson.toJson(value));
		
	}


}
