package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Input;

public class DirectClientConnector extends SuperClientConnector{
	

	public RemoteSpace 	privateServerConnections;

	public Object[] receiveWalls() throws InterruptedException {
		return sharedSpace.get(new ActualField("walls"), new FormalField(byte[].class));
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return sharedSpace.get(new ActualField(connectionId), new FormalField(byte[].class), 
				new FormalField(byte[].class), new FormalField(byte[].class));
	}

	@Override
	public void sendUserInput(Input input) {
		input.id = connectionId;
		privateServerConnections.put(input);		
	}

	@Override
	protected void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException {
		//As it is the server edition, it makes a private connection with the server.
		privateServerConnections = new RemoteSpace("tcp://" + ipaddress + ":" + port + "/clientSpace" + connectionId + "?keep");
		privateServerConnections.put("connected", connectionId);		
	}

}
