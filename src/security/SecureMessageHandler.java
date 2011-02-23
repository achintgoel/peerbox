package security;

import java.security.*;

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
	
	public byte[] signMessage(byte[] data, PrivateKey key) {
		try {
			sig.initSign(key);
			sig.update(data);
			return sig.sign();
		} catch (InvalidKeyException e) {

		} catch (SignatureException e) {

		}
		return null;
		
	}
	
	public boolean verifyMessage(byte[] data, byte[] signature, PublicKey key) {
		
		try {
			sig.initVerify(key);
			sig.update(data);
			return sig.verify(signature);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
