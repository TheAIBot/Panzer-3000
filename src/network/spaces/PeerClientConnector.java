package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import engine.GameEngine;
import engine.Input;
import network.NetworkTools;

public class PeerClientConnector extends SuperClientConnector {

	public  SpaceRepository privateRepositories[];
	public  Space privateClientConnections[];
	public 	String[] associatedUserNames;
	private GameEngine engine = new GameEngine();
	


	@Override
	protected void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
		
		//As it is the peer-to-peer version, it needs to make a private connection with every client.
		
		Object[]  privateConnectionTuple = sharedSpace.get(new FormalField(String[].class), new FormalField(boolean[].class), new FormalField(String[].class));
		String[]  privateConnectionIDs 	 = (String[])  privateConnectionTuple[0];
		boolean[] shouldCreateSpaces	 = (boolean[]) privateConnectionTuple[1];
		String[]  ipAddresses			 = (String[])  privateConnectionTuple[2];
		
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
				//It needs to get the port for the private space:
				int privatePort = (int) sharedSpace.get(new ActualField("port"), new ActualField(privateConnectionIDs[i]), new FormalField(Integer.class))[2];
				
				privateClientConnections[i] = new RemoteSpace("tcp://" + ipAddresses[i] + ":" + privatePort + "/" + privateConnectionIDs[i] + "?keep");
				privateClientConnections[i].put("Reciever", username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Creator"), new FormalField(String.class))[1];
				System.out.println(connectionId + " has connected to " + privateConnectionIDs[i]);
			}
			
		}
	}

	
	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		input.id = connectionId;
		//TODO discern between who is inputting what.
		for (Space privateClientConnection : privateClientConnections) {
			privateClientConnection.put(username, input);
		}		
	}

	@Override
	public Object[] recieveUpdates() throws InterruptedException {
		Input[] otherPlayerInputs = new Input[privateClientConnections.length];
		for (int i = 0; i < privateClientConnections.length; i++) {
			Space privateClientConnection = privateClientConnections[i];
			Object[] tuple = privateClientConnection.get(new ActualField(associatedUserNames[i]), new FormalField(Input.class));
			System.out.println(((Input) tuple[1]).id);
		}
		return null;
	}


	@Override
	public Object[] receiveWalls() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
