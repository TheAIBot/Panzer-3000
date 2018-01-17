package network.spaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
import engine.entities.Wall;
import network.NetworkProtocol;
import network.NetworkTools;

public class PeerClientConnector extends SuperClientConnector {
	
	public RemoteSpace 	sharedSpace;
	public int 			connectionId;
	public int 			numberOfClients;
	public ClientInfo clientInfo;	
	public  SpaceRepository privateRepositories[];
	public  Space privateClientConnections[];
	public 	String[] associatedUserNames;
	private GameEngine engine = new GameEngine();
	private PeerDummyServerConnector dummyServer = new PeerDummyServerConnector();
	private Input currentInput;
	private CompletableFuture<Void>[] runningTasks;
	boolean firstTick = true;
	
	public PeerClientConnector() {
		
	}
	
	@Override
	public void connect(ServerInfo serverInfo, ClientInfo clientInfo) throws UnknownHostException, IOException, InterruptedException, URISyntaxException {
		this.clientInfo = clientInfo;
		
		final URI sharedSpaceURI = NetworkTools.createURI(NetworkProtocol.TCP, serverInfo.ipAddress, serverInfo.port, "updateSpace", "keep");
		sharedSpace		= new RemoteSpace(sharedSpaceURI);
		Object[] tuple 	= sharedSpace.get(new FormalField(Integer.class), new ActualField(clientInfo.username));
		connectionId   	= (int) tuple[0];
	}

	@Override
	public void initilizePrivateConnections(String ipaddress, int port) throws Exception {
		//As it is the peer-to-peer version, it needs to make a private connection with every client.
		final Object[]  privateConnectionTuple = sharedSpace.get(new ActualField(connectionId), new FormalField(PeerConnectionInfo[].class));
		final PeerConnectionInfo[] peerConInfos = (PeerConnectionInfo[])privateConnectionTuple[1];
		
		privateRepositories 			 = new SpaceRepository[peerConInfos.length];
		privateClientConnections 		 = new Space[peerConInfos.length];
		associatedUserNames				 = new String[peerConInfos.length];
		
		for (int i = 0; i < privateClientConnections.length; i++) {
			final PeerConnectionInfo peerConInfo = peerConInfos[i];
			
			if (peerConInfo.shouldCreateSpace) {
				//It will create a gate on an unused port, and send that information to the server: 
				//the server will make sure to share the information to the other clients.
				privateRepositories[i] 	 = new SpaceRepository();
				final int gatePort = createGateWithRandomPort(privateRepositories[i], peerConInfo.ipaddressOfSpaceCreator);
				
				
				privateClientConnections[i] = new SequentialSpace();
				privateRepositories[i].add(peerConInfo.spaceName, privateClientConnections[i]);
				
				System.out.println("Chosen port: " + gatePort);
				//The port used must be announced to the server:
				sharedSpace.put("port", peerConInfo.spaceName, gatePort);
				
				privateClientConnections[i].put("Creator", clientInfo.username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Reciever"), new FormalField(String.class))[1];
			}
			else {
				//get the port the SpaceRepository was created on
				final int privatePort = (int) sharedSpace.get(new ActualField("port"), new ActualField(peerConInfo.spaceName), new FormalField(Integer.class))[2];
				
				//connect to other peer with received port
				final URI spaceRepURI = NetworkTools.createURI(NetworkProtocol.TCP, peerConInfo.ipaddressOfSpaceCreator, privatePort, peerConInfo.spaceName, "keep");
				privateClientConnections[i] = new RemoteSpace(spaceRepURI);
				
				
				privateClientConnections[i].put("Reciever", clientInfo.username);
				associatedUserNames[i] = (String) privateClientConnections[i].get(new ActualField("Creator"), new FormalField(String.class))[1];
			}
			System.out.println(connectionId + " has connected to " + peerConInfo.spaceName);
		}
		
		runningTasks = new CompletableFuture[privateClientConnections.length];
		syncronizeGame(privateClientConnections.length + 1);
	}
	
	private int createGateWithRandomPort(SpaceRepository repository, String ipaddress) {
		while (true) {
			final int candidateFreePort = NetworkTools.getRandomPort();
			try {
				repository.addGate("tcp://" + ipaddress + ":" + candidateFreePort + "/?keep");
				return candidateFreePort;
			} catch (Exception e) {
				//port in use. try again
			}					
		}
	}

	
	private void syncronizeGame(int tankCount) throws Exception {
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
		engine.prepareGame(-1, usernames, new ClientInfo[usernames.length], dummyServer, null);
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
	public Object[] recieveUpdates() throws Exception {
		Input[] playerInputs = new Input[privateClientConnections.length + 1];
		if (firstTick) {
			firstTick = false;
			for (int i = 0; i < playerInputs.length; i++) {
				playerInputs[i] = new Input();
				playerInputs[i].id = i;
			}
			dummyServer.setInputs(playerInputs);
			engine.runGameLoop(playerInputs.length, dummyServer, true);
			return dummyServer.getUpdate();
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
			dummyServer.setInputs(playerInputs);
			engine.runGameLoop(playerInputs.length, dummyServer, true);
			return dummyServer.getUpdate();
		}	
	}

	@Override
	public ArrayList<Wall> receiveWalls() throws InterruptedException {
		return dummyServer.getWalls();
	}
}
