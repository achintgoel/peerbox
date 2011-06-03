package org.peerbox.demo.cli;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;

import org.peerbox.dht.Value;
import org.peerbox.kademlia.Identifier;
import org.peerbox.kademlia.Kademlia;
import org.peerbox.kademlia.Key;
import org.peerbox.kademlia.Node;
import org.peerbox.kademlia.ResponseListener;
import org.peerbox.kademlia.messages.FindNodeResponse;
import org.peerbox.kademlia.messages.FindValueResponse;
import org.peerbox.kademlia.messages.PingResponse;
import org.peerbox.kademlia.messages.StoreResponse;

public class KadCLIHandler implements CLIHandler {
	protected Kademlia networkInstance;

	public KadCLIHandler(Kademlia networkInstance) {
		this.networkInstance = networkInstance;
	}

	@Override
	public void handleCommand(String[] args, final ExtendableCLI cli) {
		if (args.length < 2) {
			printHelp(cli.out());
			return;
		}
		if (args[1].equalsIgnoreCase("ping")) {
			if (args.length != 3) {
				printHelp(cli.out());
				return;
			}
			BigInteger ID;
			try {
				ID = new BigInteger(args[2]);
			} catch (NumberFormatException e) {
				cli.out().println("Illegal value for the Node ID");
				return;
			}
			final Node pingNode = networkInstance.getBuckets().findNodeByIdentifier(
					Identifier.fromBytes(ID.toByteArray()));
			if (pingNode == null) {
				cli.out().println("The node ID does not exist in the buckets");
			} else {
				networkInstance.ping(
						networkInstance.getBuckets().findNodeByIdentifier(Identifier.fromBytes(ID.toByteArray())),
						new ResponseListener<PingResponse>() {

							@Override
							public void onFailure() {
								cli.out().println("Ping to node at " + pingNode.getNetworkURI() + " FAILED");
							}

							@Override
							public void onResponseReceived(PingResponse response) {
								cli.out().println("Ping to node at " + pingNode.getIdentifier());
							}
						});
			}
		} else if (args[1].equalsIgnoreCase("findnode")) {
			if (args.length != 3) {
				printHelp(cli.out());
				return;
			}
			BigInteger ID;
			try {
				ID = new BigInteger(args[2]);
			} catch (NumberFormatException e) {
				cli.out().println("Illegal value for the Node ID");
				return;
			}
			networkInstance.findNode(Identifier.fromBytes(ID.toByteArray()), new ResponseListener<FindNodeResponse>() {

				@Override
				public void onFailure() {
					cli.out().println("Did not find node");
				}

				@Override
				public void onResponseReceived(FindNodeResponse response) {
					if (response.isFound()) {
						cli.out().println("Found Node at: " + response.getFoundNode().getNetworkURI());
					} else {
						cli.out().println("Did not find Node");
					}/*
					 * cli.out().println("Nearest Nodes:-"); for (Node node :
					 * response.getNearbyNodes()) { cli.out().println("\t" +
					 * node.getNetworkURI()); }
					 */
				}
			});
		} else if (args[1].equalsIgnoreCase("findvalue")) {
			if (args.length != 4) {
				printHelp(cli.out());
				return;
			}
			networkInstance.findValue(new Key(args[2], args[3]), new ResponseListener<FindValueResponse>() {

				@Override
				public void onFailure() {
					cli.out().println("Did not find the key key pair");
				}

				@Override
				public void onResponseReceived(FindValueResponse response) {
					if (response.isFound()) {
						cli.out().println("Found Values are:");
						for (Value value : response.getFoundValue()) {
							cli.out().println("\t" + value.getValue() + "\t" + new Date(value.getPublicationTime()));
						}
					} else {
						cli.out().println("Did not find the key pair");
					}/*
					 * cli.out().println("Nearest Nodes:-"); for (Node node :
					 * response.getNearbyNodes()) { cli.out().println("\t" +
					 * node.getNetworkURI()); }
					 */
				}
			});

		} else if (args[1].equalsIgnoreCase("store")) {
			if (args.length != 5) {
				printHelp(cli.out());
				return;
			}
			String key1 = args[2];
			String key2 = args[3];
			String value = args[4];
			networkInstance.storeValue(new Key(key1, key2), new Value(value), new ResponseListener<StoreResponse>() {

				@Override
				public void onFailure() {
					cli.out().println("Could not store the value");
				}

				@Override
				public void onResponseReceived(StoreResponse response) {
					if (response.successful) {
						cli.out().println("Value stored successfully");
					} else {
						cli.out().println("Could not store the value");
					}
				}
			});

		} else if (args[1].equalsIgnoreCase("buckets")) {
			networkInstance.getBuckets().print(cli.out());
		} else {
			printHelp(cli.out());
		}
	}

	public void printHelp(PrintStream out) {
		out.println("Function:\t Command");
		out.println("Print buckets:\t buckets");
		out.println("Ping a Node:\t ping [nodeID]");
		out.println("Find a Node:\t findNode [nodeID]");
		out.println("Store a Value:\t store [key1] [key2] [value]");
		out.println("Find a Value:\t findValue [key1] [key2]");
	}

}
