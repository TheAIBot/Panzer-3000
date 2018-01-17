package network.spaces;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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


public class PeerServerConnector extends SuperServerConnector {

	public static final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final int RANDOM_STRING_LENGTH = 12;
	private TreeSet<String> privateIDs = new TreeSet<String>();
	protected SequentialSpace		sharedSpace;
	protected SequentialSpace[] 	clientSpaces;
	protected String UPDATE_SPACE_NAME = "updateSpace";
	protected String INITIAL_CLIENT_SPACE_NAME = "clientSpace";
	
	@Override
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws Exception {
		this.clientInfos = clientInfos;
		this.repository 	 = new SpaceRepository();
		this.sharedSpace  = new SequentialSpace();
		this.clientSpaces = new SequentialSpace[clientInfos.length];
		
		final URI gateURI = NetworkTools.createURI(NetworkProtocol.TCP, NetworkTools.getIpAddress(), port, "", "keep");
		repository.addGate(gateURI);
		repository.add(UPDATE_SPACE_NAME, sharedSpace);
		
		
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(INITIAL_CLIENT_SPACE_NAME + i, clientSpaces[i]);
		}
		
		//Some initial information for all the clients:
		
		//The server delegates the id's
		for (int id = 0; id < clientSpaces.length; id++) {
			sharedSpace.put(id, clientInfos[id].username);
		}	
	}

	@Override
	public void initilizePrivateConnections(ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws InterruptedException {
		//It creates the different tuples with private id's (every client should take exactly one):
		ArrayList<String>[] privateIDTuples = createPrivateIDs(clientInfos.length);
		TreeMap<String, String> ipOfSpaceCreator = new TreeMap<String, String>();
		
		for (int i = 0; i < privateIDTuples.length; i++) { //Client i gets tuple i.
			
			
			ArrayList<String> privateIDTuple = privateIDTuples[i];
			
			//Only one of the two clients that receive the same server ID, should create the space/repository.
			//This is assured with the boolean array below: true means create the space, false to simply connect.
			
			final PeerConnectionInfo[] peerConInfos = new PeerConnectionInfo[clientInfos.length - 1];
			
			for (int j = 0; j < peerConInfos.length; j++) {
				peerConInfos[j] = new PeerConnectionInfo();
				final PeerConnectionInfo peerConInfo = peerConInfos[j];
				
				peerConInfo.spaceName = privateIDTuple.get(j);
							
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
		
		System.out.println("All id tuples have been placed in the shared space.");
		
		//Sending a random seed:
		
		sharedSpace.put("Random seed", 9001); //Its over 9000!
		

		for (int i = 0; i < clientInfos.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);
		}
	}

	public ArrayList<String>[] createPrivateIDs(int numClients) {
		//A complete graph is made:
		ArrayList<String>[] graphOfRandomStrings = (ArrayList<String>[]) new ArrayList[numClients];
		for (int i = 0; i < graphOfRandomStrings.length; i++) {
			graphOfRandomStrings[i] = new ArrayList<String>();
		}
		//The random strings linked to the edges.
		for (int i = 0; i < numClients; i++) {
			for (int j = i + 1; j < numClients; j++) {
				String randomString = getRandomString();				
				while (privateIDs.contains(randomString)) { 
					//The random string must be unique. The chance this happens continuesly is exponentially decreasing, 
					//so on average this step will take constant time.
					randomString = getRandomString();
				}
				graphOfRandomStrings[i].add(randomString);
				graphOfRandomStrings[j].add(randomString);
			}
		}
		return graphOfRandomStrings;
	}
	
	public static String getRandomString() {
		StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < RANDOM_STRING_LENGTH) { // length of the random string.
            int index = rnd.nextInt(SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
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
		for (int i = 0; i < clientSpaces.length; i++) {
			repository.remove(INITIAL_CLIENT_SPACE_NAME + i);
		}
		repository.closeGates();
	}
}
