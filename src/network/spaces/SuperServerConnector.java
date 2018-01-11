package network.spaces;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import javax.print.DocFlavor.STRING;

import org.jspace.*;

import engine.*;
import logger.Log;
import network.NetworkTools;
public abstract class SuperServerConnector implements Runnable {	
	public SpaceRepository 	repository;
	public String[] usernames;
	SequentialSpace		updateSpace;
	SequentialSpace[] 	clientSpaces;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	String repositioryGateURI;
	
	
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
		
		repositioryGateURI = "tcp://" + ipAddress + ":" + port + "/?keep";
		repository.addGate(repositioryGateURI);
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
		
		initilizePrivateConnections();		
	}
	
	protected abstract void initilizePrivateConnections(SequentialSpace startServerSpace) throws InterruptedException;

	public void sendWalls(ArrayList<Wall> walls) throws IOException, InterruptedException {
		for (int i = 0; i < numClients; i++) {
			byte[] wallBytes = DeSerializer.toBytes(walls);
			updateSpace.put("walls", wallBytes);
		}
	}
	
	
	public void closeConnections() {
		repository.remove(UPDATE_SPACE_NAME);
		for (int i = 0; i < clientSpaces.length; i++) {
			repository.remove(INITIAL_CLIENT_SPACE_NAME + i);
		}
		repository.closeGate(repositioryGateURI);
		
	}

}
