package network.spaces;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;

import engine.Input;
import logger.Log;


public class PeerServerConnector extends SuperServerConnector{

	public static final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final int RANDOM_STRING_LENGTH = 12;
	private TreeSet<String> privateIDs = new TreeSet<String>();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initilizePrivateConnections(SequentialSpace startServerSpace, String[] allIPAddresses) throws InterruptedException {
		//It creates the different tuples with private id's (every client should take exactly one):
		ArrayList<String>[] privateIDTuples = createPrivateIDs(numClients);
		TreeMap<String, String> idToIP 		= new TreeMap<String, String>();
		
		for (int i = 0; i < privateIDTuples.length; i++) { //Client i gets tuple i.
			
			
			ArrayList<String> privateIDTuple = privateIDTuples[i];
			
			//Only one of the two clients that receive the same server ID, should create the space/repository.
			//This is assured with the boolean array below: true means create the space, false to simply connect.
			
			boolean[] createSpaceTuple 		= new boolean[numClients - 1];
			String[]  privateIDArray 		= new  String[numClients - 1];
			String[]  associatedIPAddresses = new  String[numClients - 1];
			
			for (int j = 0; j < privateIDArray.length; j++) {

				String id = privateIDTuple.get(j);
				privateIDArray[j] 	= id;				
				if (j >= i) {
					createSpaceTuple[j] = true;
					idToIP.put(id, allIPAddresses[i]); //Notice that it is ip i, not j.	
					associatedIPAddresses[j] = allIPAddresses[i]; //If it should create the space, its own ip address is needed.
				} else {
					associatedIPAddresses[j] = idToIP.get(id); //If the other client should create the space, its ip address is needed.
				}
			}
			
			//The i corresponds to the connection id.
			sharedSpace.put(i, privateIDArray, createSpaceTuple, associatedIPAddresses);
			
		}
		
		System.out.println("All id tuples have been placed in the shared space.");
		
		//Sending a random seed:
		
		sharedSpace.put("Random seed", 9001); //Its over 9000!
		

		for (int i = 0; i < usernames.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);	
		}
	
		new Thread(() ->  {
			while (true) { //Continuously verify that the inputs are the same
				verifyInputs();
			} 
			
		}).start();;
	}

	private void verifyInputs() {
		Input[][] allInputsDifferentClients = new Input[numClients][numClients];
		for (int i = 0; i < numClients; i++) {
			try {
				allInputsDifferentClients[i] = (Input[]) sharedSpace.get(new ActualField("Inputs recieved"), new ActualField("Client " + i), new FormalField(Input[].class))[2];
			} catch (Exception e) {
				Log.exception(e);
			}
		}
		
		//Returning messages to the clients, saying that the messages have been recieved:
		
		for (int i = 0; i < numClients; i++) {
			try {
				sharedSpace.put("Seen inputs", "Client " + i);
			} catch (InterruptedException e) {
				Log.exception(e);
			}
		}
		
		//They must be the same:
		for (int i = 0; i < allInputsDifferentClients.length; i++) {
			if (allInputsDifferentClients[i].length != numClients) {
				Log.exception(new Exception("Difference in input lenghts for the different clients!!!"));
			}
		}
		//All sets of inputs must be the same as the first set of inputs:
		
		for (int i = 1; i < allInputsDifferentClients.length; i++) {
			for (int j = 0; j < allInputsDifferentClients[i].length; j++) {
				boolean sameInput = allInputsDifferentClients[0][j].equals(allInputsDifferentClients[i][j]);
				if(!sameInput) {
					Log.exception(new Exception("Different inputs between the clients"));
				}
			}
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
