package testlets;

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
	}
}
