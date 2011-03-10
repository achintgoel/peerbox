package org.peerbox.demo.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

public class ExtendableCLI {
	protected HashMap<String, CLIHandler> handlers;
	protected BufferedReader reader;
	protected AliasHandler aliasHandler;
	
	public ExtendableCLI() {
		handlers = new HashMap<String, CLIHandler>();
		reader = new BufferedReader(new InputStreamReader(System.in));
		aliasHandler = new AliasHandler();
	}
	
	public void registerHandler(String name, CLIHandler cliHandler){
		handlers.put(name, cliHandler);
	}
	
	public void registerAlias(String alias, String actual) {
		aliasHandler.registerAlias(alias, actual);
		registerHandler(alias, aliasHandler);
	}
	
	public void executeCommand(String args[]) {
		if (args.length >= 1) {
			CLIHandler handler = handlers.get(args[0]);
			if (handler != null) {
				try {
					handler.handleCommand(args, this);
				} catch(Exception e) {
					e.printStackTrace(); //TODO: REMOVE
					out().println("Error executing command.");
				}
			} else {
				if (!args[0].isEmpty()) {
					out().println("Command not found.");
				}
			}
		}
	}
	
	public void start(){
		while (true) {
			try {
				String line = reader.readLine();
				String[] args = line.split("\\s+");
				executeCommand(args);
			} catch (IOException e) {
				out().println("There was an error reading standard input.");
				break;
			}
		}
	}
	
	public PrintStream out(){
		return System.out;		
	}
	
	class AliasHandler implements CLIHandler {
		protected HashMap<String, String[]> aliases;
		
		public AliasHandler() {
			aliases = new HashMap<String, String[]>();
		}
		
		public void registerAlias(String alias, String actual) {
			aliases.put(alias, actual.split("\\s+"));
		}
		
		@Override
		public void handleCommand(String[] args, ExtendableCLI cli) {
			String[] actual = aliases.get(args[0]);
			if (actual == null) {
				cli.out().println("Invalid command.");
				return;
			}
			
			String[] result = Arrays.copyOf(actual, actual.length + args.length - 1);
			System.arraycopy(args, 1, result, actual.length, args.length - 1);
			cli.executeCommand(result);
		}
		
	}

}
