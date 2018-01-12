package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import engine.Input;

public class PeerClientConnector extends SuperClientConnector {


	public Space privateClientConnections[];
	
	
	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		input.id = connectionId;
		//TODO discern between who is inputting what.
		for (Space privateClientConnection : privateClientConnections) {
			privateClientConnection.put(input);
		}		
	}


	@Override
	protected void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
		//As it is the peer-to-peer version, it 
		Object[]  privateConnectionTuple = sharedSpace.get(new FormalField(String[].class), new FormalField(boolean[].class));
		String[]  privateConnectionIDs 	 = (String[])  privateConnectionTuple[0];
		boolean[] shouldCreateSpaces	 = (boolean[]) privateConnectionTuple[1];
		privateClientConnections = new RemoteSpace[privateConnectionIDs.length];
		for (int i = 0; i < privateClientConnections.length; i++) {
			if (shouldCreateSpaces[i]) {
				privateClientConnections[i] = new RemoteSpace("tcp://" + ipaddress + ":" + port + "/" + privateConnectionIDs[i] + "?keep");
			}
		}
	}


	@Override
	public Object[] recieveUpdates() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object[] receiveWalls() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
