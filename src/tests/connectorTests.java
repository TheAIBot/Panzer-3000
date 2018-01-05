package tests;


import static org.junit.Assert.assertEquals;

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
		Thread.sleep(1000);
		assertEquals(numOfClients, server.numConnectedClients);
		
	}
	
	
	@Test
	public void testSendAndRecieveUpdates() throws InterruptedException {
		testSetupInitialClientServerConnection();
		
		//Creating an unique test input.
		Tank[] tanks = new Tank[2];
		List<Bullet> bullets = new ArrayList<Bullet>();
		tanks[0] = new Tank(0, 0, 0, 1, 0, 0, 0);
		tanks[1] = new Tank(0, 0, 0, 2, 0, 0, 0);
		bullets.add(new Bullet(0, 0, 0, 1, 0));
		bullets.add(new Bullet(0, 0, 0, 2, 0));
		
		
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
			assertEquals(clients[i].connectionId, inputs[i].id);
		}
		
		
		
	}

}
