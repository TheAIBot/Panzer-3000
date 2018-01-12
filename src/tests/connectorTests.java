package tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import engine.Bullet;
import engine.DeSerializer;
import engine.Input;
import engine.Powerup;
import engine.Tank;
import engine.Wall;
import network.spaces.SuperClientConnector;
import network.spaces.SuperServerConnector;

public class connectorTests {
	/*
	SuperServerConnector server;
	SuperClientConnector[] clients;
	String[] clientNames = new String[] {"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8"};
	
	@Test
	public void testSetupInitialClientServerConnection() throws InterruptedException {
		int numOfClients = 8;
		server = new SuperServerConnector();
		server.usernames = clientNames;
		server.numClients = numOfClients;
		server.ipAddress = "localhost";
		new Thread(server).start();
		
		clients = new SuperClientConnector[numOfClients];
		Thread.sleep(1000); //The server needs to be set up, before the clients tries to connect.
		for (int i = 0; i < numOfClients; i++) {
			final int k = i;
			clients[i] =  new SuperClientConnector();
			clients[i].username = clientNames[i];
			new Thread(clients[i]).start();;
		}
		//As it runs on different threads, and essentially a deadlock will be reached if this doesn't work,
		//it will instead be checked that after 1 second (2 might be better), all connections are established.
		Thread.sleep(1500);
		assertEquals(numOfClients, server.numConnectedClients);
		server.closeConnections();
		//server.closeConnections();
		
	}
	
	
	@Test
	public void testSendAndRecieveUpdates() throws Exception {
		testSetupInitialClientServerConnection();
		
		//Creating an unique test input.
		ArrayList<Tank> tanks = new ArrayList<Tank>();
		ArrayList<Bullet> bullets = new ArrayList<Bullet>();
		ArrayList<Powerup> powerups = new ArrayList<Powerup>();
		tanks.add(new Tank(0, 0, 0, 0, 0));
		tanks.add(new Tank(0, 0, 0, 0, 0));
		bullets.add(new Bullet(0, 0, 1, 0, 10));
		bullets.add(new Bullet(0, 0, 2, 0, 10));
		powerups.add(new Powerup(0.5, 0.5, 1));
		powerups.add(new Powerup(0.5, 0.5, 1));
		tanks.get(0).userName = clientNames[0];
		tanks.get(1).userName = clientNames[1];
		
		server.sendUpdates(tanks, bullets, powerups);
		
		//Now all the clients should be able to get the updates.
		
		for (int i = 0; i < tanks.size(); i++) {
			Object[] updateTuple = clients[i].recieveUpdates();
			ArrayList<Tank> recievedTanks = DeSerializer.toList((byte[])updateTuple[1], Tank.class);
			ArrayList<Bullet> recievedBullets = DeSerializer.toList((byte[])updateTuple[2], Bullet.class);
			ArrayList<Powerup> recievedPowerups = DeSerializer.toList((byte[])updateTuple[3], Powerup.class);
			
			double acceptedMarginOfError = 0.01;
			assertEquals(tanks.get(0).bodyHeight, recievedTanks.get(0).bodyHeight, acceptedMarginOfError);
			assertEquals(tanks.get(1).bodyHeight, recievedTanks.get(1).bodyHeight, acceptedMarginOfError);
			assertEquals(bullets.get(0).size  , recievedBullets.get(0).size  , acceptedMarginOfError);
			assertEquals(bullets.get(1).size  , recievedBullets.get(1).size  , acceptedMarginOfError);
			assertEquals(powerups.get(0).x  , recievedPowerups.get(0).x  , acceptedMarginOfError);
			assertEquals(powerups.get(1).x  , recievedPowerups.get(1).x  , acceptedMarginOfError);
		}		
		//TODO The cleanup from recieving inputs also needs to be tested.
	}
	

	@Test
	public void testSendAndRecieveUserInputs() throws InterruptedException {
		testSetupInitialClientServerConnection();
		
		
		for (int i = 0; i < clients.length; i++) {
			SuperClientConnector client = clients[i];
			Input input = new Input(false, false, false, false, false, 0, 0, client.connectionId);
			client.sendUserInput(input);
		}		
		
		Input[] inputs = server.reciveUserInputs();
		assertEquals(clients.length, inputs.length);
		for (int i = 0; i < clients.length; i++) {
			boolean idExists = false;
			for (Input input : inputs) {
				if (clients[i].connectionId == input.id) {
					idExists = true;
				}
			}
			assertTrue(idExists);
		}
		
		
		
	}
*/
}
