package network.spaces;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import engine.DeSerializer;
import engine.Input;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;
import network.NetworkProtocol;
import network.NetworkTools;
import security.Crypto;


public class PeerServerConnector extends SuperServerConnector {
	private SequentialSpace sharedSpace;
	
	public static final int RANDOM_STRING_LENGTH = 12;
	public static final String UPDATE_SPACE_NAME = "updateSpace";
	
	@Override
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws Exception {
		this.clientInfos = clientInfos;
		this.repository = new SpaceRepository();
		this.sharedSpace = new SequentialSpace();
		
		final URI gateURI = NetworkTools.createURI(NetworkProtocol.TCP, NetworkTools.getIpAddress(), port, "", "keep");
		repository.addGate(gateURI);
		repository.add(UPDATE_SPACE_NAME, sharedSpace);
		
		String[] usernames = new String[clientInfos.length];	
		
		for (int i = 0; i < clientInfos.length; i++) {
			usernames[i] = clientInfos[i].username;
		}
		
		//The server delegates the id's
		for (int id = 0; id < clientInfos.length; id++) {
			sharedSpace.put(id, clientInfos[id].username, usernames);
		}	
	}

	@Override
	public void initilizePrivateConnections(ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws Exception {
		//It creates the different tuples with private id's (every client should take exactly one):
		final ArrayList<String>[] privateIDTuples = createPrivateIDs(clientInfos.length);
		final HashMap<String, String> ipOfSpaceCreator = new HashMap<String, String>();
		
		for (int i = 0; i < privateIDTuples.length; i++) { //Client i gets tuple i.
			final ArrayList<String> privateIDTuple = privateIDTuples[i];
			final PeerConnectionInfo[] peerConInfos = new PeerConnectionInfo[clientInfos.length - 1];
			
			for (int j = 0; j < peerConInfos.length; j++) {
				peerConInfos[j] = new PeerConnectionInfo();
				final PeerConnectionInfo peerConInfo = peerConInfos[j];
				
				peerConInfo.spaceName = privateIDTuple.get(j);
						
				//Only one of the two clients that receive the same server ID, should create the space/repository.
				//This is assured with the boolean array below: true means create the space, false to simply connect.
				if (j >= i) {
					peerConInfo.shouldCreateSpace = true;
					peerConInfo.ipaddressOfSpaceCreator = clientInfos[i].ipaddress;
					
					//a peer responsible for creating a space was just created so
					//add it to the list of created spaces to other peers can connecto to it
					ipOfSpaceCreator.put(peerConInfo.spaceName, clientInfos[i].ipaddress);
				} else {
					peerConInfo.shouldCreateSpace = false;
					//get the ipaddress of the peer that created the space so
					//this peer can connect to it
					peerConInfo.ipaddressOfSpaceCreator = ipOfSpaceCreator.get(peerConInfo.spaceName);
				}
			}
			
			//The i corresponds to the connection id.
			sharedSpace.put(i, peerConInfos);
		}
		
		//Sending a random seed
		sharedSpace.put("Random seed", (int)(Math.random() * 100000));
		
		for (int i = 0; i < clientInfos.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String>[] createPrivateIDs(int numClients) throws NoSuchAlgorithmException, NoSuchProviderException {
		final HashSet<String> randomStrings = new HashSet<String>();
		final ArrayList<String>[] graphOfRandomStrings = (ArrayList<String>[]) new ArrayList[numClients];
		for (int i = 0; i < graphOfRandomStrings.length; i++) {
			graphOfRandomStrings[i] = new ArrayList<String>();
		}
		
		//The random strings linked to the edges.
		for (int i = 0; i < numClients; i++) {
			for (int j = i + 1; j < numClients; j++) {
				String randomString = null;	
				do {
					randomString = Crypto.getRandomString(RANDOM_STRING_LENGTH);
					//The random string must be unique. The chance this happens continuesly is exponentially decreasing, 
					//so on average this step will take constant time.
				} while (randomStrings.contains(randomString));
				randomStrings.add(randomString);
				
				graphOfRandomStrings[i].add(randomString);
				graphOfRandomStrings[j].add(randomString);
			}
		}
		return graphOfRandomStrings;
	}

	@Override
	public void sendWalls(ArrayList<Wall> walls) throws Exception {
		for (int i = 0; i < clientInfos.length; i++) {
			byte[] wallBytes = DeSerializer.toBytes(walls);
			sharedSpace.put("walls", wallBytes);
		}
	}

	@Override
	public void sendUpdate(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups) throws Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Input[] receiveUserInputs() throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void closeConnections() {
		repository.remove(UPDATE_SPACE_NAME);
		repository.closeGates();
	}
}
