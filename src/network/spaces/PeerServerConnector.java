package network.spaces;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import org.jspace.SequentialSpace;


public class PeerServerConnector extends SuperServerConnector{

	public static final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final int RANDOM_STRING_LENGTH = 12;
	private TreeSet<String> privateIDs = new TreeSet<String>();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initilizePrivateConnections(SequentialSpace startServerSpace) throws InterruptedException {
		//It creates the different tuples with private id's (every client should take exactly one):
		ArrayList<String>[] privateIDTuples = createPrivateIDs(numClients);
		for (int i = 0; i < privateIDTuples.length; i++) {
			ArrayList<String> privateIDTuple = privateIDTuples[i];
			//Only one of the two clients that receive the same server ID, should create the space/repository.
			//This is assured with the boolean array below: true means create the space, false to simply connect.
			boolean[] createSpaceTuple 	= new boolean[numClients - 1];
			String[] privateIDArray 	= new  String[numClients - 1];
			String[] ipAddresses		= new  String[numClients - 1];
			for (int j = i; j < createSpaceTuple.length; j++) {
				createSpaceTuple[j] = true;
			}
			for (int j = 0; j < ipAddresses.length; j++) {
				privateIDArray[j] 	= privateIDTuple.get(j);
				ipAddresses[j] 		= "localhost";
			}
			sharedSpace.put( privateIDArray, createSpaceTuple, ipAddresses);
		}
		System.out.println("All id tuples have been placed in the shared space.");
		
		//Sending a random seed:
		
		sharedSpace.put("Random seed", 9001); //Its over 9000!
		

		for (int i = 0; i < usernames.length; i++) {
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
}
