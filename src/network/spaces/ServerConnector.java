package network.spaces;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import org.jspace.*;

<<<<<<< HEAD:src/connector/ServerConnector.java

import Logger.Log;
=======
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ServerConnector.java
import engine.*;
import logger.Log;
import network.NetworkTools;
public class ServerConnector implements Runnable {
	
	public final static String IP_ADDRESS = "localhost"; //"192.168.43.196";
	public final static String CONNECTION_TYPE = "tcp";
	
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
		
<<<<<<< HEAD:src/connector/ServerConnector.java
		repository.addGate(CONNECTION_TYPE + "://" + ipAddress + ":9001/?keep");
=======
		repository.addGate("tcp://" + ipAddress + ":" + port + "/?keep");
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ServerConnector.java
		repository.add(UPDATE_SPACE_NAME, updateSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
<<<<<<< HEAD:src/connector/ServerConnector.java
		}
		
		//Some initial information for all the clients:
		
		//Number of users to connect:
		updateSpace.put("numClients", numClients);
=======
		}		
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ServerConnector.java
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id, usernames[id]);
		}
		
<<<<<<< HEAD:src/connector/ServerConnector.java
		System.out.println("0 clients are connected");
=======
		for (int i = 0; i < usernames.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);	
		}
		
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ServerConnector.java
		//And waits for all clients to connect:
		for (int id = 0; id < clientSpaces.length; id++) {				
				Object[] tuple = clientSpaces[id].get(new ActualField("connected"), new ActualField(id));
				numConnectedClients++;
<<<<<<< HEAD:src/connector/ServerConnector.java
				System.out.println(numConnectedClients + " clients have connected");
=======
				Log.message("Someone connected");
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ServerConnector.java
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
