package security;
import java.io.Serializable;

public class SignedMessage implements Serializable {
	protected byte[] message;
	protected byte[] signature;
	
	public SignedMessage(byte[] mess, byte[] sig) {
		message = mess;
		signature = sig;
	}

	public byte[] getMessage() {
		return message;
	}

	public byte[] getSignature() {
		return signature;
	}
}