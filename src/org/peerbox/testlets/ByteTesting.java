package org.peerbox.testlets;

import java.math.BigInteger;

public class ByteTesting {
	public static void main(String[] args) {
		byte[] bytes = new byte[2];
		bytes[0] = 0x01;
		bytes[1] = 0x10;
		int x = 0;
		x |= bytes[0];
		x <<= 8;
		x |= bytes[1];
		System.out.println(x);
		System.out.println(bytes[0]);
		System.out.println(bytes[1]);
		System.out.println(new BigInteger(bytes));
		
		
		int hello = 0 - Byte.MAX_VALUE;
		byte blah = (byte) hello;
		System.out.println(hello);
		System.out.println(blah);
		hello = (int) blah + Byte.MAX_VALUE;
		System.out.println(hello);
	}
}
