package starters;

import java.util.Scanner;

import engine.Client;

public class ClientStarter {
	public static void main(String[] args) {
		System.out.print("Enter your username: ");
		Scanner in = new Scanner(System.in);
		System.out.println("kage");
		String username;
		//username = in.next();
		username = "kage";
		new Client().startGame(username);
	}
}