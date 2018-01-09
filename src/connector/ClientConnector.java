package connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jspace.*;

import Logger.Log;
import engine.*;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	public String		username;
	
	public void connectToServer(String ipaddress, String username) throws UnknownHostException, IOException, InterruptedException {
		Thread.sleep(1000);
		this.username = username;
		updateSpace		= new RemoteSpace("tcp://" + ipaddress + ":9002/updateSpace?keep");
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class), new ActualField(username));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new RemoteSpace("tcp://" + ipaddress + ":9002/clientSpace" + connectionId + "?keep");
		privateServerConnections.put("connected", connectionId);
	}
	
	public Object[] receiveWalls() throws InterruptedException {
		return updateSpace.get(new ActualField("walls"), new FormalField(byte[].class));
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return updateSpace.get(new ActualField(connectionId), new FormalField(byte[].class), new FormalField(byte[].class));
	}
	
	
	public void sendUserInput(Input input) {
		input.id = connectionId;
		privateServerConnections.put(input);		
	}

	@Override
	public void run() {
		try {
			connectToServer("localhost", username);			
		} catch (Exception e) { 
			Log.exception(e);
		}
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
