package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;


import engine.GameEngine;
import logger.Log;
import engine.Input;
import engine.PeerGameEngine;
import network.NetworkTools;
import sun.util.logging.resources.logging;

public class PeerClientConnector extends SuperClientConnector {

	
	public  SpaceRepository privateRepositories[];
	public  Space privateClientConnections[];
	public 	String[] associatedUserNames;
	private GameEngine engine = new GameEngine();
	private Input currentInput;
	private CompletableFuture<Void>[] runningTasks;
	boolean firstTick = true;
	
	@Override
	public void connectToServer(ServerInfo serverInfo, ClientInfo clientInfo) throws UnknownHostException, IOException, InterruptedException {
		this.clientInfo = clientInfo;
		
		sharedSpace		= new RemoteSpace("tcp://" + serverInfo.ipAddress + ":" + serverInfo.port + "/updateSpace?keep");
		Object[] tuple 	= sharedSpace.get(new FormalField(Integer.class), new ActualField(clientInfo.username));
		connectionId   	= (int) tuple[0];
		initilizePrivateConnections(serverInfo.ipAddress, serverInfo.port);
	}

	@Override
	public void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
		
		//As it is the peer-to-peer version, it needs to make a private connection with every client.
		
		Object[]  privateConnectionTuple = sharedSpace.get(new ActualField(connectionId), new FormalField(String[].class), new FormalField(boolean[].class), new FormalField(String[].class));
		String[]  privateConnectionIDs 	 = (String[])  privateConnectionTuple[1];
		boolean[] shouldCreateSpaces	 = (boolean[]) privateConnectionTuple[2];
		String[]  ipAddresses			 = (String[])  privateConnectionTuple[3];
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
				
				privateClientConnections[i].put("Creator", clientInfo.username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Reciever"), new FormalField(String.class))[1];
				System.out.println(connectionId + " has connected to " + privateConnectionIDs[i]);
				
			} 
			//If the other client is making the private space:
			else {				
				Thread.sleep(500); //TODO Hack: needs to be removed
				//It needs to get the port for the private space:
				int privatePort = (int) sharedSpace.get(new ActualField("port"), new ActualField(privateConnectionIDs[i]), new FormalField(Integer.class))[2];
				
				privateClientConnections[i] = new RemoteSpace("tcp://" + ipAddresses[i] + ":" + privatePort + "/" + privateConnectionIDs[i] + "?keep");
				privateClientConnections[i].put("Reciever", clientInfo.username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Creator"), new FormalField(String.class))[1];
				System.out.println(connectionId + " has connected to " + privateConnectionIDs[i]);
			}
			
		}
		runningTasks = new CompletableFuture[privateClientConnections.length];
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
		usernames[usernames.length - 1] = clientInfo.username;
		Arrays.sort(usernames); //Ensures a deterministic ordering of the usernames
		engine.initializeGame(usernames);
		
		//Initial inputs needs to be send. 
		//TODO this leads to the possibility of there being two inpts from the same user in the same space. This needs to be fixed.
	}


	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		input.id = connectionId;
		
		for (int i = 0; i < associatedUserNames.length; i++) {
			final int k = i;
			runningTasks[i] = CompletableFuture.runAsync(() -> {
				try {
					privateClientConnections[k].put(clientInfo.username, input);
				} catch (InterruptedException e) {
					Log.exception(e);
				}
			});
		}
		currentInput = input;
	}

	@Override
	public Object[] recieveUpdates() throws InterruptedException {
		Input[] playerInputs = new Input[privateClientConnections.length + 1];
		if (firstTick) {
			firstTick = false;
			for (int i = 0; i < playerInputs.length; i++) {
				playerInputs[i] = new Input();
				playerInputs[i].id = i;
			}
			return engine.getUpdates(playerInputs);
		} else {
			try {
				CompletableFuture.allOf(runningTasks).get();
				
				for (int i = 0; i < privateClientConnections.length; i++) {
					final int k = i;
					runningTasks[i] = CompletableFuture.runAsync(() -> {
						try {
							Space privateClientConnection = privateClientConnections[k];
							Object[] tuple = privateClientConnection.get(new ActualField(associatedUserNames[k]), new FormalField(Input.class));
							Input receivedInput = (Input) tuple[1];
							//Sorting them:
							playerInputs[receivedInput.id] = receivedInput;							
						} catch (Exception e) {
							Log.exception(e);					
						}
					});
				}
				
			} catch (ExecutionException e) {
				Log.exception(e);
			}
			
			//Also include the inputs from player, that is using the machine the program is running on!
			playerInputs[currentInput.id] = currentInput; 		
			
			try {
				CompletableFuture.allOf(runningTasks).get(); //The engine needs all the inputs!
			} catch (Exception e) {
				Log.exception(e);
			}			
			return engine.getUpdates(playerInputs);
		}	
	}

	@Override
	public Object[] receiveWalls() throws InterruptedException {
		return engine.getWalls();
	}
}
