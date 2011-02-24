package kademlia.messages;

import kademlia.Identifier;

public abstract class FindRequest extends Request {
	public abstract Identifier getTargetIdentifier();

}
