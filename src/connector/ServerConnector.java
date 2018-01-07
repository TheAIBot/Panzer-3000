package connector;

import java.util.*;

import org.jspace.*;

import Logger.Log;
import engine.*;
public class ServerConnector implements Runnable {
	
	public SpaceRepository 	repository;
	SequentialSpace		updateSpace;
	SequentialSpace[] 	clientSpaces;
	String UPDATE_SPACE_NAME = "updateSpace";
	String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	
	public int numClients;
	public int numConnectedClients;
	
	
	public void initializeServerConnection(int numClients) throws InterruptedException {
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
		for (int id = 0; id < clientSpaces.length; id++) {				
				clientSpaces[id].get(new ActualField("connected"), new FormalField(Double.class));
				numConnectedClients++;
		}
		//Now communication is up and running.
	}
	
	public void sendUpdates(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Wall> walls) throws InterruptedException {
		for (int i = 0; i < numClients; i++) {
			updateSpace.put(i, tanks, bullets, walls);
		}
	}
	
	
	public Input[] reciveUserInputs() throws InterruptedException {
		Input[] recievedInputs = new Input[numClients];
		for (int i = 0; i < numClients; i++) {
			//Log.message("Input count: " + clientSpaces[i].size());
			final Object[] tuple = clientSpaces[i].get(new FormalField(Input.class));
			//Log.message("Input count: " + clientSpaces[i].size());
			final Input input = (Input) tuple[0];
			recievedInputs[input.id] = input;
		}
		
		return recievedInputs;
	}

	@Override
	public void run() {
		try {
			initializeServerConnection(numClients);	
		} catch (Exception e) {
			Log.exception(e);
		}
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
