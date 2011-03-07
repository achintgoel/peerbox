package testlets;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

public class SigningTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
			
			
			KeyPair pair = keyGen.generateKeyPair();
			
			Signature sig = Signature.getInstance("SHA1withDSA");
			sig.initSign(pair.getPrivate());
			sig.update("hello".getBytes());
			byte[] signedHello = sig.sign();
			
			Signature verify = Signature.getInstance("SHA1withDSA");
			verify.initVerify(pair.getPublic());
			verify.update("hello".getBytes());
			boolean verified = verify.verify(signedHello);
			
			System.out.println(verified);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
