package org.peerbox.kademlia.messages;

import org.peerbox.kademlia.Identifier;

public abstract class FindRequest extends Request {
	public abstract Identifier getTargetIdentifier();
}
