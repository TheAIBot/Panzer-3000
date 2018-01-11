package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Input;

public class PeerClientConnector extends SuperClientConnector {


	public RemoteSpace 	privateClientConnections[];
	
	
	@Override
	public void sendUserInput(Input input) {
		input.id = connectionId;
		//TODO discern between who is inputting what.
		for (RemoteSpace privateClientConnection : privateClientConnections) {
			privateClientConnection.put(input);
		}		
	}


	@Override
	protected void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
		Object[] tuple = sharedSpace.get(new FormalField(String[].class));
		String[] privateConnectionIDs = (String[]) tuple[0];
		privateClientConnections = new RemoteSpace[privateConnectionIDs.length];
		for (int i = 0; i < privateClientConnections.length; i++) {
			privateClientConnections[i] = new RemoteSpace("tcp://" + ipaddress + ":" + port + "/" + privateConnectionIDs[i] + "?keep");
		}
		//As it is the peer-to-peer version, it 
	}

}
