package tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import connector.BasicClient;
import connector.BasicServer;
import connector.ServerInfo;

public class basicTests {
	BasicServer server;
	BasicClient client;
	
	@Test
	public void testSetupInitialConnection() throws InterruptedException {
		/*
		server = new BasicServer();
		client = new BasicClient();
		
		System.out.println("Starting server");
		try {
			server.startServer();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Server started");
		Thread.sleep(1000); //The server needs to be set up, before the clients tries to connect.

		System.out.println("Creating server info");
		ServerInfo curr = new ServerInfo();
		curr.name = "name";
		curr.ipAddress = "localhost";
		curr.clientsConnected = 1;
		

		System.out.println("Starting client");
		client.startClient();
		System.out.println("Client started");
		
		System.out.println("Client joining game");
		try {
			client.joinGame(curr, "username1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Client joined game");
		

		System.out.println("Client starting game");
		client.startGame(curr);
		System.out.println("Client started game");
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
