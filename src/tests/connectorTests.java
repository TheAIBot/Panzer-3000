package tests;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import connector.ClientConnector;
import connector.ServerConnector;
import engine.Bullet;
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
		//it will instead be checked that after 10 seconds, all connections are established.
		Thread.sleep(1000);
		assertEquals(numOfClients, server.numConnectedClients);
		
	}
	
	
	@Test
	public void testSendAndRecieveUpdates() throws InterruptedException {
		testSetupInitialClientServerConnection();
		
		//Creating an unique test input.
		Tank[] tanks = new Tank[2];
		List<Bullet> bullets = new ArrayList<Bullet>();
		tanks[0] = new Tank();
		tanks[1] = new Tank();
		bullets.add(new Bullet());
		bullets.add(new Bullet());
		tanks[0].bodyHeight = 1;
		tanks[1].bodyHeight = 2;
		bullets.get(0).height = 1;
		bullets.get(1).height = 2;
		
		
		server.sendUpdates(tanks, bullets);
		
		//Now all the clients should be able to get the updates.
		
		for (int i = 0; i < tanks.length; i++) {
			Object[] updateTuple = clients[i].recieveUpdates();
			Tank[] recievedTanks = clients[i].unpackTanks(updateTuple);
			List<Bullet> recievedBullets = clients[i].unpackBullets(updateTuple);
			
			double acceptedMarginOfError = 0.01;
			assertEquals(1, recievedTanks[0].bodyHeight, acceptedMarginOfError);
			assertEquals(2, recievedTanks[1].bodyHeight, acceptedMarginOfError);
			assertEquals(1, recievedBullets.get(0).height, acceptedMarginOfError);
			assertEquals(2, recievedBullets.get(1).height, acceptedMarginOfError);
		}		
		//TODO The cleanup from recieving inputs also needs to be tested.
	}

}
