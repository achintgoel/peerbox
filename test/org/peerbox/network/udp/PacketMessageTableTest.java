package org.peerbox.network.udp;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PacketMessageTableTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPut() {
		// to test that a message is removed after completion in jumbled order;
		// ---------------------
		PacketMessageTable table = new PacketMessageTable(64, 256, 600);
		Random random = new Random();
		Short bytes = ((Integer)random.nextInt()).shortValue();
		table.setLength(bytes, 20);
		for(int i = 0; i < 20; i+=2){
			table.put(bytes, i, null);
		}
		for(int i = 1; i < 20; i+=2){
			table.put(bytes, i, null);
		}
		Assert.assertEquals(0, table.getNumMessages());
		
		// to test that after adding more than 64 packets the size still stays 64
		// ----------------
		for(Integer i = 0; i < 70; i++){
			bytes = ((Integer)random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(64, table.getNumMessages());
		
		
		// to test that the timeout clears all the old messages but keeps new ones
		// --------------------------------
		table = new PacketMessageTable(64, 256, 1);
		for(int i = 0; i < 64; i++){
			bytes = ((Integer)random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(64, table.getNumMessages());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 20; i++){
			bytes = ((Integer)random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(20, table.getNumMessages());
		
		
	}

}
