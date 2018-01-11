package network.spaces;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import org.jspace.*;

import engine.*;
import logger.Log;
import network.NetworkTools;
public class ServerConnector implements Runnable {	
	public SpaceRepository 	repository;
	public String[] usernames;
	SequentialSpace		updateSpace;
	SequentialSpace[] 	clientSpaces;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	
	public int numClients;
	public int numConnectedClients;
	public String ipAddress;
	
	
	public void initializeServerConnection(int port, int numClients, String[] usernames, SequentialSpace startServerSpace) throws InterruptedException, UnknownHostException, SocketException {
		this.numClients = numClients;
		this.numConnectedClients = 0;
		this.ipAddress = NetworkTools.getIpAddress();
		this.usernames = usernames;
		
		repository 	 = new SpaceRepository();
		updateSpace  = new SequentialSpace();
		clientSpaces = new SequentialSpace[numClients];
		this.usernames = usernames;
		
		repository.addGate("tcp://" + ipAddress + ":" + port + "/?keep");
		repository.add(UPDATE_SPACE_NAME, updateSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		//Some initial information for all the clients:
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id, usernames[id]);
		}
		
		for (int i = 0; i < usernames.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);	
		}
		
		//And waits for all clients to connect:
		for (int id = 0; id < clientSpaces.length; id++) {				
				clientSpaces[id].get(new ActualField("connected"), new ActualField(id));
				numConnectedClients++;
		}
		System.out.println("All has connected.");
		//Now communication is up and running. It will remove the extra information added for the sake of the clients:
		updateSpace.get(new ActualField("numClients"), new ActualField(numClients));
	}
	
	public void sendWalls(ArrayList<Wall> walls) throws IOException, InterruptedException {
		for (int i = 0; i < numClients; i++) {
			byte[] wallBytes = DeSerializer.toBytes(walls);
			updateSpace.put("walls", wallBytes);
		}
	}
	
	public void sendUpdates(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups) throws InterruptedException, IOException {
		for (int i = 0; i < numClients; i++) {
			byte[] tankBytes = DeSerializer.toBytes(tanks);
			byte[] bulletBytes = DeSerializer.toBytes(bullets);
			byte[] powerupBytes = DeSerializer.toBytes(powerups);
			//Log.message("Package size: " + (tankBytes.length + bulletBytes.length));
			updateSpace.put(i, tankBytes, bulletBytes, powerupBytes);
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
			//initializeServerConnection(numClients, usernames);	
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
