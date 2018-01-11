package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.*;

<<<<<<< HEAD:src/connector/ClientConnector.java
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.corba.se.spi.activation.Server;
import com.sun.swing.internal.plaf.metal.resources.metal;

import Logger.Log;
=======
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ClientConnector.java
import engine.*;
import logger.Log;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	public String		username;
	public int 			numberOfClients;
	
	public void connectToServer(String ipaddress, int port, String username) throws UnknownHostException, IOException, InterruptedException {
		this.username = username;
<<<<<<< HEAD:src/connector/ClientConnector.java
		updateSpace		= new RemoteSpace("tcp://" + ipaddress + ":9001/updateSpace?keep");
		List<Object[]> tuples = updateSpace.queryAll(new FormalField(Object.class), new FormalField(Object.class));
		Object[] tuple1 = updateSpace.query(new ActualField("numClients"), new FormalField(Integer.class));
		numberOfClients = (int) tuple1[1];
=======
		updateSpace		= new RemoteSpace("tcp://" + ipaddress + ":" + port + "/updateSpace?keep");
>>>>>>> refs/remotes/origin/udpbroadcast:src/network/spaces/ClientConnector.java
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class), new ActualField(username));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new RemoteSpace("tcp://" + ipaddress + ":" + port + "/clientSpace" + connectionId + "?keep");
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
