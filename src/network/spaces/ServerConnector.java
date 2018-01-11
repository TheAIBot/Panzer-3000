package network.spaces;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.security.PublicKey;
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
	
	private PublicKey[] publicKeys;
	
	
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
		

		Log.message("Server recieving salts and creating spaces");
		for (int i = 0; i < usernames.length; i++) {
			
			Log.message("Server querying for username: " + usernames[i]);
			Object[] tuple = updateSpace.get(new ActualField(usernames[i]), new FormalField(String.class));
			String salt = (String) tuple[1];
			Log.message("Salt recieved is: " + salt);
			
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i + salt, clientSpaces[i]);
			Log.message("Client space added by Server");
			
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
				Object[] keyTuple = clientSpaces[id].get(new FormalField(PublicKey.class), new ActualField(id));
				publicKeys[id] = (PublicKey) keyTuple[0];
			
				clientSpaces[id].get(new ActualField("connected"), new ActualField(id));
				numConnectedClients++;
		}
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
