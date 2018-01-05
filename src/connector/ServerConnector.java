package connector;

import java.util.*;

import org.jspace.*;

import engine.*;
public class ServerConnector implements Runnable {
	
	public SpaceRepository 	repository;
	SequentialSpace[] 	clientSpaces;
	SequentialSpace		updateSpace;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	
	public int numClients;
	public int numConnectedClients;
	
	public static void main(String[] args) {
		new ServerConnector().initializeServerConnection(1);
	}
	
	
	public void initializeServerConnection(int numClients) {
		this.numClients = numClients;
		this.numConnectedClients = 0;
		
		repository 	 = new SpaceRepository();
		updateSpace  = new SequentialSpace();
		clientSpaces = new SequentialSpace[numClients];
		
		repository.addGate("tcp://localhost:9001/?keep");
		repository.add(UPDATE_SPACE_NAME, updateSpace);
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			updateSpace.put(id);
		}
		
		//And waits for all clients to connect:
		
		for (double id = 0; id < clientSpaces.length; id++) {
			try {
				//FormalField field1 = new FormalField(String.class);
				ActualField field1 = new ActualField("connected");
				FormalField field2 = new FormalField(Double.class);
				//ActualField field2 = new ActualField(id);
				
				clientSpaces[(int) id].get(field1, field2);
				numConnectedClients++;
				//System.out.println("cake");
			} catch (InterruptedException e) {
				e.printStackTrace(); 
			}
		}
		//Now communication is up and running.		
		
		//System.out.println("All connections established");
	}
	
	public void sendUpdates(Tank[] tanks, List<Bullet> bullets) {
		updateSpace.put(tanks, bullets);	
	}
	
	
	public Input[] reciveUserInputs() {
		//Removes everything from the update space. Thus, when the clients ask for the updates, 
		//they will have to wait until the server makes an update based on the current  input.
		updateSpace.getAll(new FormalField(Tank[].class), new FormalField(Tank[].class)); 
		
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

	@Override
	public void run() {
		initializeServerConnection(numClients);
	}
	
	/*
	public void closeConnections() {
		repository.remove(UPDATE_SPACE_NAME);
		for (int i = 0; i < clientSpaces.length; i++) {
			repository.remove(INITIAL_CLIENT_SPACE_NAME + i);
		}
	}
	*/
	
	//sendUpdates(Tank[] tanks, Bullet[] bullets); //Sends information to all the clients, about tanks, bullets and the likes.
	//Inputs recieveUserInputs();
}
