package tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import connector.ClientConnector;
import connector.ServerConnector;
import engine.Bullet;
import engine.Input;
import engine.Tank;

public class connectorTests {
	ServerConnector server;
	ClientConnector[] clients;
	
	@Test
	public void testSetupInitialClientServerConnection() throws InterruptedException {
		int numOfClients = 8;
		server = new ServerConnector();
		server.numClients = numOfClients;
		clients = new ClientConnector[numOfClients];
		new Thread(server).start();
		Thread.sleep(1000); //The server needs to be set up, before the clients tries to connect.
		for (int i = 0; i < numOfClients; i++) {
			clients[i] =  new ClientConnector();
			new Thread(clients[i]).start();
		}
		//As it runs on different threads, and essentially a deadlock will be reached if this doesn't work,
		//it will instead be checked that after 1 second (2 might be better), all connections are established.
		Thread.sleep(1500);
		assertEquals(numOfClients, server.numConnectedClients);
		
	}
	
	
	@Test
	public void testSendAndRecieveUpdates() throws InterruptedException {
		testSetupInitialClientServerConnection();
		
		//Creating an unique test input.
		ArrayList<Tank> tanks = new ArrayList<Tank>();
		ArrayList<Bullet> bullets = new ArrayList<Bullet>();
		tanks.add(new Tank(0, 0, 0, 1, 0, 0, 0));
		tanks.add(new Tank(0, 0, 0, 2, 0, 0, 0));
		bullets.add(new Bullet(0, 0, 0, 1, 0));
		bullets.add(new Bullet(0, 0, 0, 2, 0));
		
		
		server.sendUpdates(tanks, bullets);
		
		//Now all the clients should be able to get the updates.
		
		for (int i = 0; i < tanks.size(); i++) {
			Object[] updateTuple = clients[i].recieveUpdates();
			ArrayList<Tank> recievedTanks = clients[i].unpackTanks(updateTuple);
			ArrayList<Bullet> recievedBullets = clients[i].unpackBullets(updateTuple);
			
			double acceptedMarginOfError = 0.01;
			assertEquals(tanks.get(0).bodyHeight, recievedTanks.get(0).bodyHeight, acceptedMarginOfError);
			assertEquals(tanks.get(1).bodyHeight, recievedTanks.get(1).bodyHeight, acceptedMarginOfError);
			assertEquals(bullets.get(0).height, recievedBullets.get(0).height, acceptedMarginOfError);
			assertEquals(bullets.get(1).height, recievedBullets.get(1).height, acceptedMarginOfError);
		}		
		//TODO The cleanup from recieving inputs also needs to be tested.
	}
	

	@Test
	public void testSendAndRecieveUserInputs() throws InterruptedException {
		testSetupInitialClientServerConnection();
		
		
		for (int i = 0; i < clients.length; i++) {
			ClientConnector client = clients[i];
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

}
