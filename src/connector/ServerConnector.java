package connector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import org.jspace.*;

import Logger.Log;
import engine.*;
public class ServerConnector implements Runnable {
	public SpaceRepository 	repository;
	SequentialSpace		updateSpace;
	SequentialSpace[] 	clientSpaces;
	String[] usernames;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	
	public int numClients;
	public int numConnectedClients;
	public String ipAddress;
	
	
	public void initializeServerConnection(int numClients, String[] usernames) throws InterruptedException, UnknownHostException {
		this.numClients = numClients;
		this.numConnectedClients = 0;
		this.ipAddress = BasicServer.getIpAddress();
		this.usernames = usernames;
		
		repository 	 = new SpaceRepository();
		updateSpace  = new SequentialSpace();
		clientSpaces = new SequentialSpace[numClients];
		
		repository.addGate("tcp://" + ipAddress + ":9001/?keep");
		repository.add(UPDATE_SPACE_NAME, updateSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id, usernames[id]);
		}
		
		//And waits for all clients to connect:
		for (int id = 0; id < clientSpaces.length; id++) {				
				clientSpaces[id].get(new ActualField("connected"), new ActualField(id));
				numConnectedClients++;
		}
		//Now communication is up and running.
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
			Log.message("Package size: " + (tankBytes.length + bulletBytes.length));
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
			initializeServerConnection(numClients, usernames);	
		} catch (Exception e) {
			Log.exception(e);
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
