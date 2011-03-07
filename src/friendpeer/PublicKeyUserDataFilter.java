package friendpeer;

import dht.MapDataFilter;

public class PublicKeyUserDataFilter implements MapDataFilter<String, String> {

	@Override
	public boolean isValid(String key, String value) {
		// TODO: Deserialize to PublicKey / SignedMessage and validate!
		return true;
	}

}
