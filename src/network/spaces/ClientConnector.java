package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.List;

import org.jspace.*;

import engine.*;
import logger.Log;

public class ClientConnector implements Runnable{

	public SecureRemoteSpace 	privateServerConnections;
	public SecureRemoteSpace 	updateSpace;
	public int 					connectionId;
	public String				username;
	public int 					numberOfClients;
	private KeyPair				keyPair;
	private String				salt;
	
	public void connectToServer(String ipaddress, int port, String username, KeyPair keyPair, String salt) throws UnknownHostException, IOException, InterruptedException {
		this.username = username;
		this.keyPair = keyPair;
		this.salt = salt;
		updateSpace		= new SecureRemoteSpace("tcp://" + ipaddress + ":" + port + "/updateSpace?keep");
		
		Log.message("Client sending own salt");
		// Client puts salt associated with itself into the update space
		updateSpace.put(username, salt);
		
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class), new ActualField(username));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new SecureRemoteSpace("tcp://" + ipaddress + ":" + port + "/clientSpace" + connectionId + salt + "?keep");
		privateServerConnections.put("connected", connectionId);
	}
	
	public Object[] receiveWalls() throws InterruptedException {
		return updateSpace.get(new ActualField("walls"), new FormalField(byte[].class));
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return updateSpace.get(new ActualField(connectionId), new FormalField(byte[].class), 
				new FormalField(byte[].class), new FormalField(byte[].class));
	}
	
	
	public void sendUserInput(Input input) {
		input.id = connectionId;
		privateServerConnections.put(input);		
	}

	@Override
	public void run() {
		try {
			//connectToServer("localhost", username);
		} catch (Exception e) { 
			Log.exception(e);
		}
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
