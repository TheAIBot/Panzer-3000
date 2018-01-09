package connector;

import java.io.IOException;
import java.util.*;

import org.jspace.*;


import Logger.Log;
import engine.*;
public class ServerConnector implements Runnable {
	
	public final static String IP_ADDRESS = "localhost"; //"192.168.43.196";
	public final static String CONNECTION_TYPE = "tcp";
	
	public SpaceRepository 	repository;
	SequentialSpace		updateSpace;
	SequentialSpace[] 	clientSpaces;
	String[] usernames;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	
	public int numClients;
	public int numConnectedClients;
	public String ipAddress;
	
	
	public void initializeServerConnection(int numClients, String ipAddress, String[] usernames) throws InterruptedException {
		this.numClients = numClients;
		this.numConnectedClients = 0;
		this.ipAddress = ipAddress;
		this.usernames = usernames;
		
		repository 	 = new SpaceRepository();
		updateSpace  = new SequentialSpace();
		clientSpaces = new SequentialSpace[numClients];
		this.usernames = usernames;
		
		repository.addGate(CONNECTION_TYPE + "://" + ipAddress + ":9001/?keep");
		repository.add(UPDATE_SPACE_NAME, updateSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		//Some initial information for all the clients:
		
		//Number of users to connect:
		updateSpace.put("numClients", numClients);
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id, usernames[id]);
		}
		
		System.out.println("0 clients are connected");
		//And waits for all clients to connect:
		for (int id = 0; id < clientSpaces.length; id++) {				
				Object[] tuple = clientSpaces[id].get(new ActualField("connected"), new ActualField(id));
				numConnectedClients++;
				System.out.println(numConnectedClients + " clients are connected");
		}
		System.out.println("All has connected.");
		//Now communication is up and running. It will remove the extra information added for the sake of the clients:
		updateSpace.get(new ActualField("numClients"), new ActualField(numClients));
	}
	
	public void sendWalls(ArrayList<Wall> walls) throws IOException {
		for (int i = 0; i < numClients; i++) {
			byte[] wallBytes = DeSerializer.toBytes(walls);
			updateSpace.put("walls", wallBytes);
		}
	}
	
	public void sendUpdates(ArrayList<Tank> tanks, ArrayList<Bullet> bullets) throws InterruptedException, IOException {
		for (int i = 0; i < numClients; i++) {
			byte[] tankBytes = DeSerializer.toBytes(tanks);
			byte[] bulletBytes = DeSerializer.toBytes(bullets);
			//Log.message("Package size: " + (tankBytes.length + bulletBytes.length));
			updateSpace.put(i, tankBytes, bulletBytes);
		}
	}
	
	
	public Input[] reciveUserInputs() throws InterruptedException {
		Input[] recievedInputs = new Input[numClients];
		for (int i = 0; i < numClients; i++) {
			//Log.message("Input count: " + clientSpaces[i].size());
			final Object[] tuple = clientSpaces[i].get(new FormalField(Input.class));
			//Log.message("Input count: " + clientSpaces[i].size());
			final Input input = (Input) tuple[0];
			recievedInputs[input.id] = input;
		}
		
		return recievedInputs;
	}

	@Override
	public void run() {
		try {
			initializeServerConnection(numClients, ipAddress, usernames);	
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	public void setUserNames(ArrayList<Tank> tanks, String[] usernames) {
		for (int i = 0; i < tanks.size(); i++) {
			tanks.get(i).userName = usernames[tanks.get(i).id];
		}		
	}
	
	/*
	public void closeConnections() {
		repository.remove(UPDATE_SPACE_NAME);
		for (int i = 0; i < clientSpaces.length; i++) {
			repository.remove(INITIAL_CLIENT_SPACE_NAME + i);
		}
	}
	*/
	
	//sendUpdates(Tank[] tanks, Bullet[] bullets); //Sends information to all the clients, about tanks, bullets and the likes.
	//Inputs recieveUserInputs();
}
