package org.peerbox.demo.cli;

public interface CLIHandler {

	public void handleCommand(String[] args, ExtendableCLI cli);
}
