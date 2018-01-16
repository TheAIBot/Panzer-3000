package starters;

import engine.Client;
import engine.SuperGameEngine;

public class TestStarter {
	public static void main(String[] args) {
		byte[] a = new byte[] {1, 2, 3, 4};
		byte[] b = new byte[] {1, 2, 3, 4};
		byte[] c = new byte[] {1, 2, 4, 3};
		System.out.println(a.equals(b));
		System.out.println(a.equals(c));
		System.out.println("asdas".equals("asdas"));
	}
}
