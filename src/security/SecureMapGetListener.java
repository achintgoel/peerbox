package security;

import java.security.*;

public interface SecureMapGetListener {
	public SignedMessage valueFound(SignedMessage val);
}
