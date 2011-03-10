package org.peerbox.demo.cli;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.peerbox.friend.Friend;
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
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(args[3]));
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
				cli.out().println("Specified key is invalid. Please check with your friend.");
			} catch (NumberFormatException e) {
				cli.out().println("Specified key is invalid. Please check with your friend.");
			}
			
		} else if(function.equalsIgnoreCase("delete")) {
			if(args.length != 3) {
				help();
				return;
			}
			String alias = args[2];
			Friend friend = friend_manager.getFriend(alias);
			if(friend != null) {
				friend_manager.removeFriend(alias);
			}
			else {
				cli.out().println("The friend "+alias+" does not exist. Imaginary friend?");
			}
			
		} else if(function.equalsIgnoreCase("list")) {
			if(args.length > 2) {
				help();
				return;
			}
			friend_manager.printBuddyList();
		} else if (function.equalsIgnoreCase("signOn") || function.equalsIgnoreCase("myKey")) {
			if (args.length > 2) {
				help();
				return;
			}
			friend_manager.signOn();
		} else {
			help();
		}
		
	}
	public void help(){
		System.out.println("Illegal Command");
	}

}
