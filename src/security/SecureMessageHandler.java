package security;

import java.security.*;

public class SecureMessageHandler {
	protected Signature dsa;
	protected KeyPairGenerator keyGen;

	public SecureMessageHandler() {
		try {
			keyGen = KeyPairGenerator.getInstance("DSA");
			dsa = Signature.getInstance("SHA1withDSA");
			
		} catch (NoSuchAlgorithmException e) {
			
		}

	}
	
	public byte[] signMessage(byte[] data, PrivateKey key) {
		try {
			dsa.initSign(key);
			dsa.update(data);
			return dsa.sign();
		} catch (InvalidKeyException e) {

		} catch (SignatureException e) {

		}
		return null;
		
	}
	
	public boolean verifyMessage(byte[] data, byte[] signature, PublicKey key) {
		
		try {
			dsa.initVerify(key);
			dsa.update(data);
			return dsa.verify(signature);
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
