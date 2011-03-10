package org.peerbox.demo.cli;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.peerbox.friend.FriendManager;

public class FriendCLIHandler implements CLIHandler{
	
	
	protected FriendManager friend_manager;
	public FriendCLIHandler(FriendManager manager) {
		this.friend_manager = manager;
	}
	@Override
	public void handleCommand(String[] args, ExtendableCLI cli) {
		// TODO Auto-generated method stub
		if(args.length < 2) {
			help();
			return;
		}
		String function = args[1];
		if(function.equalsIgnoreCase("add")) {
			
			if(args.length != 4) {
				help();
				return;
			}
			try {
				String alias = args[2];
				BigInteger key = new BigInteger(args[3]);
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key.toByteArray());
				KeyFactory keyFactory;
				PublicKey publicKey;
				keyFactory = KeyFactory.getInstance("DSA");
				publicKey = keyFactory.generatePublic(pubKeySpec);
				friend_manager.createFriend(alias, null, publicKey);
				cli.out().println("Added "+alias+" to buddy list");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				cli.out().println("Unable to generate key based on algorithm specified");
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				cli.out().println("Generated key is invalid");
			} catch (NumberFormatException e) {
				cli.out().println("Public Key provided has invalid format");
			}
			
		}
		else if(function.equalsIgnoreCase("delete")) {
			if(args.length != 3) {
				help();
				return;
			}
			String alias = args[2];
			if(friend_manager.getFriend(alias) != null) {
				friend_manager.removeFriend(alias);
			}
			else {
				cli.out().println("The friend "+alias+" does not exist");
			}
			
		}
		else if(function.equalsIgnoreCase("list")) {
			if(args.length > 2) {
				help();
				return;
			}
			friend_manager.printBuddyList();
		}
		else {
			help();
		}
		
	}
	public void help(){
		System.out.println("Illegal Command");
	}

}
