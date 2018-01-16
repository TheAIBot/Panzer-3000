package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.*;

import engine.*;
import logger.Log;

public abstract class SuperClientConnector implements Runnable{

	public RemoteSpace 	sharedSpace;
	public int 			connectionId;
	public String		username;
	public int 			numberOfClients;
	
	public void connectToServer(ServerInfo serverInfo, ClientInfo clientInfo) throws UnknownHostException, IOException, InterruptedException {
		this.username = username;
		sharedSpace		= new RemoteSpace("tcp://" + serverInfo.ipAddress + ":" + serverInfo.port + "/updateSpace?keep");
		Object[] tuple 	= sharedSpace.get(new FormalField(Integer.class), new ActualField(username));
		connectionId   	= (int) tuple[0];
		initilizePrivateConnections(serverInfo.ipAddress, serverInfo.port);
	}
	
	protected abstract void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException;

	@Override
	public void run() {
		try {
			//connectToServer("localhost", username);			
		} catch (Exception e) { 
			Log.exception(e);
		}
	}

	public abstract void sendUserInput(Input input) throws InterruptedException; 
	
	public abstract Object[] recieveUpdates() throws InterruptedException;

	public abstract Object[] receiveWalls() throws InterruptedException;
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
