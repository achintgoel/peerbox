package org.peerbox.demo.cli;

import org.peerbox.chat.ChatManager;

public class ChatCLIHandler implements CLIHandler {
	private ChatManager chat;

	public ChatCLIHandler(ChatManager chat) {
		this.chat = chat;
	}

	@Override
	public void handleCommand(String[] args, ExtendableCLI cli) {
		if (args.length < 3) {
			cli.out().println("Missing friend name or message");
			return;
		}
		StringBuilder message = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			message.append(args[i]);
			message.append(' ');
		}
		chat.sendMessage(args[1], message.toString());
	}

}
