package kadmelia;

import java.math.BigInteger;

public class Identifier implements Identifiable {
	
	public BigInteger getIntegerValue() {
		return null;
	}
	
	public static BigInteger calculateDistance(Identifier x, Identifier y) {
		return x.getIntegerValue().xor(y.getIntegerValue());
	}
	
	public static BigInteger calculateDistance(Identifiable x, Identifiable y) {
		return calculateDistance(x.getIdentifier(), y.getIdentifier());
	}
 	
	public Identifier getIdentifier() {
		return this;
	}
}
