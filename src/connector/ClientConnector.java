package connector;

import java.util.*;

import org.jspace.*;

import engine.*;

public class ClientConnector {
	RemoteSpace privateServerConnections;
	RemoteSpace updateSpace;
	int 		connectionId;
	
	public static void main(String[] args) {
		new ClientConnector().connectToServer();
	}
	
	public void connectToServer() {
		try {
			updateSpace		= new RemoteSpace("tcp://127.0.0.1:9001/updateSpace?keep");
			Object[] tuple 	= updateSpace.get(new FormalField(Integer.class));
			connectionId   	= (int) tuple[0];
			privateServerConnections = new RemoteSpace("tcp://127.0.0.1:9001/clientSpace" + connectionId + "?keep");
			privateServerConnections.put("connected", 0.0);
			System.out.println("Client connected");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void recieveUpdates(Tank[] tanks, List<Bullet> bullets) {
		try {
			//TODO ask if reading puts a lock on the space.
			Object[] tuple = updateSpace.query(new FormalField(Tank[].class), new FormalField(List.class));
			tanks 	= (Tank[]) tuple[0];
			bullets = (List<Bullet>) tuple[1];
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendUserInputs(Input inputs) {
		privateServerConnections.put(inputs);		
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
