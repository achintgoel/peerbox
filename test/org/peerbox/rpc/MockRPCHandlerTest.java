package org.peerbox.rpc;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

public class MockRPCHandlerTest {
	RPCHandler handlerA;
	RPCHandler handlerB;
	
	@Before
	public void setUp() throws Exception {
		MockRPCRouter router = new MockRPCRouter();
		handlerA = router.getNewRPCHandler();
		handlerB = router.getNewRPCHandler();
	}
	
	@Test
	public void simpleSendMessageNoServiceTest() {
		final Stack<RPCEvent> appleServiceOnA = new Stack<RPCEvent>();
		handlerB.sendRequest(handlerA.getLocalURI(), "fakeService", "blahblah", new RPCResponseListener() {
			@Override
			public void onResponseReceived(RPCEvent event) {
				appleServiceOnA.push(event);
			}

			@Override
			public void onTimeout() {
				// TODO Auto-generated method stub
				
			}
		});
		assert(appleServiceOnA.size() == 0);
	}
	
	@Test
	public void serviceMessageTest() {
		final Stack<RPCEvent> appleServiceOnA = new Stack<RPCEvent>();
		final Stack<RPCEvent> orangeServiceOnA = new Stack<RPCEvent>();
		handlerA.registerServiceListener("appleService", new RPCServiceRequestListener() {
			@Override
			public void onRequestRecieved(RPCEvent e) {
				appleServiceOnA.push(e);
			}	
		});
		handlerA.registerServiceListener("orangeService", new RPCServiceRequestListener() {
			@Override
			public void onRequestRecieved(RPCEvent e) {
				orangeServiceOnA.push(e);
			}	
		});
		handlerB.sendRequest(handlerA.getLocalURI(), "appleService", "secret message", new RPCResponseListener() {
			@Override
			public void onResponseReceived(RPCEvent event) {}
			@Override
			public void onTimeout() {}
		});
		assert(appleServiceOnA.size() == 1);
		RPCEvent eventOne = appleServiceOnA.pop();
		assertEquals(eventOne.getDataString(), "secret message");
		assertEquals(eventOne.getServiceName(), "appleService");
		assertEquals(eventOne.getSenderURI(), handlerB.getLocalURI());
		assert(orangeServiceOnA.size() == 0);
		
		handlerB.sendRequest(handlerA.getLocalURI(), "appleService", "second message", new RPCResponseListener() {
			@Override
			public void onResponseReceived(RPCEvent event) {}
			@Override
			public void onTimeout() {}
		});
		assert(appleServiceOnA.size() == 1);
		RPCEvent eventTwo = appleServiceOnA.pop();
		assertEquals(eventTwo.getDataString(), "second message");
		assertEquals(eventTwo.getServiceName(), "appleService");
		assertEquals(eventTwo.getSenderURI(), handlerB.getLocalURI());
		assert(orangeServiceOnA.size() == 0);
		
		handlerB.sendRequest(handlerA.getLocalURI(), "orangeService", "orange message", new RPCResponseListener() {
			@Override
			public void onResponseReceived(RPCEvent event) {}
			@Override
			public void onTimeout() {}
		});
		assert(orangeServiceOnA.size() == 1);
		RPCEvent eventThree = orangeServiceOnA.pop();
		assertEquals(eventThree.getDataString(), "orange message");
		assertEquals(eventThree.getServiceName(), "orangeService");
		assertEquals(eventThree.getSenderURI(), handlerB.getLocalURI());
		assert(appleServiceOnA.size() == 0);
	}
	
	@Test
	public void serviceReplyTest() {
		final Stack<RPCEvent> appleBResponse = new Stack<RPCEvent>();
		handlerA.registerServiceListener("appleService", new RPCServiceRequestListener() {
			@Override
			public void onRequestRecieved(RPCEvent e) {
				e.respond("hello world!");
			}	
		});
		handlerA.registerServiceListener("orangeService", new RPCServiceRequestListener() {
			@Override
			public void onRequestRecieved(RPCEvent e) {
				e.respond("goodbye world!");
			}	
		});
		handlerB.sendRequest(handlerA.getLocalURI(), "appleService", "secret message", new RPCResponseListener() {
			@Override
			public void onResponseReceived(RPCEvent event) {
				appleBResponse.push(event);
			}
			@Override
			public void onTimeout() {}
		});
		assert(appleBResponse.size() == 1);
		RPCEvent eventOne = appleBResponse.pop();
		assertEquals(eventOne.getDataString(), "hello world!");
		assertEquals(eventOne.getServiceName(), "appleService");
		assertEquals(eventOne.getSenderURI(), handlerA.getLocalURI());
	}

}
