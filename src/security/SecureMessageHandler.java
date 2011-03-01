package security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SecureMessageHandler {
	protected Signature sig;
	protected KeyPairGenerator keyGen;

	public SecureMessageHandler() {
		try {
			keyGen = KeyPairGenerator.getInstance("DSA");
			sig = Signature.getInstance("SHA1withDSA");
			
		} catch (NoSuchAlgorithmException e) {
			
		}

	}
	
	public byte[] signMessage(String data, PrivateKey key) {
		try {
			sig.initSign(key);
			sig.update(data.getBytes("UTF8"));
			return sig.sign();
		} catch (InvalidKeyException e) {

		} catch (SignatureException e) {

		} catch (UnsupportedEncodingException e) {

		}
		return null;
		
	}
	/**
	 * verifyMessage: verifies a signature given a public key with the original message
	 * @param message represented as a String.  Will be converted to a UTF-8 encoded byte array for verification
	 * @param signature represented as a byte array.
	 * @param key
	 * @return 
	 */
	public boolean verifyMessage(String message, byte[] signature, PublicKey key) {
		
		try {
			sig.initVerify(key);
			sig.update(message.getBytes("UTF8"));
			return sig.verify(signature);
		} catch (InvalidKeyException e) {
			
		} catch (SignatureException e) {
			
		} catch (UnsupportedEncodingException e) {
			
		}
		return false;
	}
}
