package org.peerbox.security;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SecureMessageHandler {
	protected Signature ver;
	protected Signature sig;
	protected KeyPairGenerator keyGen;
	protected KeyPair pair;
	
	public SecureMessageHandler(KeyPair pair) {
		try {
			sig = Signature.getInstance("SHA1withDSA");
			this.pair = pair;
			sig.initSign(this.pair.getPrivate());
			//ver = Signature.getInstance("SHA1withDSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SecureMessageHandler() {
		try {
			sig = Signature.getInstance("SHA1withDSA");
			keyGen = KeyPairGenerator.getInstance("DSA");
			pair = keyGen.generateKeyPair();
			sig.initSign(this.pair.getPrivate());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public byte[] signMessage(String data) {
		
			
			try {
				sig.update(data.getBytes());
				return sig.sign();
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			ver = Signature.getInstance("SHA1withDSA");
			ver.initVerify(key);
			ver.update(message.getBytes());
			//System.out.println("signature is:"+signature.toString());
			return ver.verify(signature);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public KeyPair getKeyPair() {
		return pair;
	}
}
