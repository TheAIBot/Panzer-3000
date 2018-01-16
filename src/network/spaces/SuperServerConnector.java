package network.spaces;

import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import org.jspace.*;

import engine.*;
import engine.entities.Wall;
import network.NetworkProtocol;
import network.NetworkTools;

public abstract class SuperServerConnector {	
	public SpaceRepository 	repository;
	protected SequentialSpace		sharedSpace;
	protected SequentialSpace[] 	clientSpaces;
	protected String UPDATE_SPACE_NAME = "updateSpace";
	protected String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	protected ClientInfo[] clientInfos;
	
	
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws InterruptedException, UnknownHostException, SocketException, URISyntaxException {		
		this.clientInfos = clientInfos;
		this.repository 	 = new SpaceRepository();
		this.sharedSpace  = new SequentialSpace();
		this.clientSpaces = new SequentialSpace[clientInfos.length];
		
		final URI gateURI = NetworkTools.createURI(NetworkProtocol.TCP, NetworkTools.getIpAddress(), port, "", "keep");
		repository.addGate(gateURI);
		repository.add(UPDATE_SPACE_NAME, sharedSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		//Some initial information for all the clients:
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			sharedSpace.put(id, clientInfos[id].username);
		}
		
		initilizePrivateConnections(startServerSpace, clientInfos);		
	}
	
	protected abstract void initilizePrivateConnections(SequentialSpace startServerSpace, ClientInfo[] clientInfos) throws InterruptedException;

	public void sendWalls(ArrayList<Wall> walls) throws IOException, InterruptedException {
		for (int i = 0; i < clientInfos.length; i++) {
			byte[] wallBytes = DeSerializer.toBytes(walls);
			sharedSpace.put("walls", wallBytes);
		}
	}
	
	
	public void closeConnections() {
		repository.remove(UPDATE_SPACE_NAME);
		for (int i = 0; i < clientSpaces.length; i++) {
			repository.remove(INITIAL_CLIENT_SPACE_NAME + i);
		}
		repository.closeGates();
		
	}
	
	

}
