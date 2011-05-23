package org.peerbox.kademlia;

import java.util.Comparator;

public class IdentifiableDistanceComparator implements Comparator<Identifiable> {
	final private Identifiable origin;

	public IdentifiableDistanceComparator(Identifiable origin) {
		if (origin == null) {
			throw new IllegalArgumentException("origin cannot be null");
		}
		this.origin = origin;
	}

	@Override
	public int compare(Identifiable x, Identifiable y) {
		return Identifier.calculateDistance(origin, x).compareTo(Identifier.calculateDistance(origin, y));
	}

}
