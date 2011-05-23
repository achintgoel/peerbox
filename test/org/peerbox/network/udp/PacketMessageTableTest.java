package org.peerbox.network.udp;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PacketMessageTableTest {
	Random random = new Random();

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test that a message is removed after completion in jumbled order
	 * 
	 * @author vineet
	 */
	@Test
	public void testJumbledMessage() {
		PacketMessageTable table = new PacketMessageTable(64, 256, 600);

		Short bytes = ((Integer) random.nextInt()).shortValue();
		table.setLength(bytes, 20);
		for (int i = 0; i < 20; i += 2) {
			table.put(bytes, i, null);
		}
		for (int i = 1; i < 20; i += 2) {
			table.put(bytes, i, null);
		}
		Assert.assertEquals(0, table.getNumMessages());
	}

	/**
	 * Test that after adding more than 64 packets the size still stays 64
	 * 
	 * @author vineet
	 */
	@Test
	public void testLotsOfPackets() {
		PacketMessageTable table = new PacketMessageTable(64, 256, 600);
		Short bytes;
		for (Integer i = 0; i < 70; i++) {
			bytes = ((Integer) random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(64, table.getNumMessages());
	}

	/**
	 * Test that the timeout clears all the old messages but keeps new ones
	 * 
	 * @author vineet
	 */
	@Test
	public void testTimeout() {
		Short bytes;
		PacketMessageTable table = new PacketMessageTable(64, 256, 1);
		for (int i = 0; i < 64; i++) {
			bytes = ((Integer) random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(64, table.getNumMessages());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 20; i++) {
			bytes = ((Integer) random.nextInt()).shortValue();
			table.setLength(bytes, 2);
			table.put(bytes, 0, null);
		}
		Assert.assertEquals(20, table.getNumMessages());
	}

}
