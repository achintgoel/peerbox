package kademlia.messages;

public class PingRequest extends Request {
	final protected String command = "ping";
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}

}
