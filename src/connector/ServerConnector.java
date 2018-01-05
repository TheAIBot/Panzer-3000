package connector;

import java.util.*;

import org.jspace.*;

import engine.*;
public class ServerConnector {
	SpaceRepository 	repository;
	SequentialSpace[] 	clientSpaces;
	SequentialSpace		updateSpace;
	
	int numClients;
	
	public static void main(String[] args) {
		new ServerConnector().initializeServerConnection(1);
	}
	
	public void initializeServerConnection(int numClients) {
		this.numClients = numClients;
		
		repository 	 = new SpaceRepository();
		updateSpace  = new SequentialSpace();
		clientSpaces = new SequentialSpace[numClients];
		
		repository.addGate("tcp://localhost:9001/?keep");
		repository.add("updateSpace", updateSpace);
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add("clientSpace" + i, clientSpaces[i]);
		}
		
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id);
		}
		
		//And waits for all clients to connect:
		
		for (int id = 0; id < clientSpaces.length; id++) {
			try {
				FormalField field1 = new FormalField(String.class);
				FormalField field2 = new FormalField(Double.class);
				
				clientSpaces[id].get(field1, field2);
				System.out.println("Kage");
			} catch (InterruptedException e) {
				e.printStackTrace(); 
			}
		}
		//Now communication is up and running.		
		
		System.out.println("All connections established");
	}
	
	public void sendUpdates(Tank[] tanks, List<Bullet> bullets) {
		updateSpace.put(tanks, bullets);	
	}
	
	
	public Input[] reciveUserInputs() {
		Input[] recievedInputs = new Input[numClients];
		for (int i = 0; i < numClients; i++) {
			Object[] tuple;
			try {
				tuple = clientSpaces[i].get(new FormalField(Input.class));
				recievedInputs[i] = (Input) tuple[0];
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		return recievedInputs;
	}
	
	
	//sendUpdates(Tank[] tanks, Bullet[] bullets); //Sends information to all the clients, about tanks, bullets and the likes.
	//Inputs recieveUserInputs();
}
