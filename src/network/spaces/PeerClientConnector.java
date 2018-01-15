package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;


import engine.SuperGameEngine;
import engine.Input;
import engine.PeerGameEngine;
import network.NetworkTools;

public class PeerClientConnector extends SuperClientConnector {

	public  SpaceRepository privateRepositories[];
	public  Space privateClientConnections[];
	public 	String[] associatedUserNames;
	private PeerGameEngine engine = new PeerGameEngine();
	private Input currentInput;
	

	@Override
	protected void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
		
		//As it is the peer-to-peer version, it needs to make a private connection with every client.
		
		Object[]  privateConnectionTuple = sharedSpace.get(new FormalField(String[].class), new FormalField(boolean[].class), new FormalField(String[].class));
		String[]  privateConnectionIDs 	 = (String[])  privateConnectionTuple[0];
		boolean[] shouldCreateSpaces	 = (boolean[]) privateConnectionTuple[1];
		String[]  ipAddresses			 = (String[])  privateConnectionTuple[2];
		System.out.println("The ip addresses:");
		for (int i = 0; i < ipAddresses.length; i++) {
			System.out.println(ipAddresses[i]);			
		}
		
		privateRepositories 			 = new SpaceRepository[privateConnectionIDs.length];
		privateClientConnections 		 = new Space[privateConnectionIDs.length];
		associatedUserNames				 = new String[privateConnectionIDs.length];
		
		for (int i = 0; i < privateClientConnections.length; i++) {
			
			//If this is the client that should create the private space:
			
			if (shouldCreateSpaces[i]) { 
				
				//It will create a gate on an unused port, and send that information to the server: 
				//the server will make sure to share the information to the other clients.
				
				privateRepositories[i] 	 = new SpaceRepository();
				boolean hasFoundFreePort = false;
				int candidateFreePort = 0;
				while (!hasFoundFreePort) {
					candidateFreePort = NetworkTools.getRandomPort();
					try {
						privateRepositories[i].addGate("tcp://" + ipAddresses[i] + ":" + candidateFreePort + "/?keep");
						hasFoundFreePort = true;
					} catch (Exception e) {
						//Try again with a new port!!!
					}					
				}
				privateClientConnections[i] = new SequentialSpace();
				privateRepositories[i].add(privateConnectionIDs[i], privateClientConnections[i]);
				
				System.out.println("Chosen port: " + candidateFreePort);
				//The port used must be announced to the server:
				sharedSpace.put("port", privateConnectionIDs[i], candidateFreePort);
				//TODO currently it is just taken by the other client, without going through the server.
				//One might also simply encrypt it using the other clients public key.
				
				privateClientConnections[i].put("Creator", username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Reciever"), new FormalField(String.class))[1];
				System.out.println(connectionId + " has connected to " + privateConnectionIDs[i]);
				
			} 
			//If the other client is making the private space:
			else {				
				Thread.sleep(500); //TODOHack: needs to be removed
				//It needs to get the port for the private space:
				int privatePort = (int) sharedSpace.get(new ActualField("port"), new ActualField(privateConnectionIDs[i]), new FormalField(Integer.class))[2];
				
				privateClientConnections[i] = new RemoteSpace("tcp://" + ipAddresses[i] + ":" + privatePort + "/" + privateConnectionIDs[i] + "?keep");
				privateClientConnections[i].put("Reciever", username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Creator"), new FormalField(String.class))[1];
				System.out.println(connectionId + " has connected to " + privateConnectionIDs[i]);
			}
			
		}
		
		syncronizeGame(privateClientConnections.length + 1);
	}

	
	private void syncronizeGame(int tankCount) throws InterruptedException {
		//Include some code for sending out the random seed.
		Object[] randomSeedTuple = sharedSpace.query(new ActualField("Random seed"), new FormalField(Integer.class));
		int randomSeed = (int) randomSeedTuple[1];
		engine.setRandomSeed(randomSeed);
		String[] usernames = new String[associatedUserNames.length + 1];
		for (int i = 0; i < associatedUserNames.length; i++) {
			usernames[i] = associatedUserNames[i];
		}
		usernames[usernames.length - 1] = username;
		Arrays.sort(usernames); //Ensures a deterministic ordering of the usernames
		engine.initializeGame(tankCount, usernames);
		//Initial inputs needs to be send. 
		//TODO this leads to the possibility of there being two inpts from the same user in the same space. This needs to be fixed.
		sendUserInput(new Input()); 
	}


	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		input.id = connectionId;
		//TODO discern between who is inputting what.
		for (Space privateClientConnection : privateClientConnections) {
			privateClientConnection.put(username, input);
		}		
		currentInput = input;
	}

	@Override
	public Object[] recieveUpdates() throws InterruptedException {
		Input[] playerInputs = new Input[privateClientConnections.length + 1];
		for (int i = 0; i < privateClientConnections.length; i++) {
			Space privateClientConnection = privateClientConnections[i];
			Object[] tuple = privateClientConnection.get(new ActualField(associatedUserNames[i]), new FormalField(Input.class));
			playerInputs[i] = (Input) tuple[1];
			System.out.println(((Input) tuple[1]).id);
		}
		//Also include the inputs from player, that is using the machine the program is running on!
		playerInputs[privateClientConnections.length] = currentInput; 
		
		return engine.getUpdates(playerInputs);
	}


	@Override
	public Object[] receiveWalls() throws InterruptedException {
		return engine.getWalls();
	}

}
