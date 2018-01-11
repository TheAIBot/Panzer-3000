package network.spaces;

import java.util.ArrayList;
import java.util.Random;

import org.jspace.SequentialSpace;

import com.sun.java.swing.plaf.gtk.resources.gtk_it;

public class PeerServerConnector extends SuperServerConnector{

	private final String SALTCHARS = "abcdefghijklmnopqrstuvqxyzæøå0123456789";
	public  final int RANDOM_STRING_LENGTH = 12;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initilizePrivateConnections(SequentialSpace startServerSpace) throws InterruptedException {
		//It creates the different tuples with private id's:
		String[][] privateIDTuples = createPrivateIDs(numClients);
	}

	private ArrayList<String>[] createPrivateIDs(int numClients) {
		//A complete graph is made:
		ArrayList<String>[] graphOfRandomStrings = (ArrayList<String>[]) new ArrayList[numClients];
		Random random = new Random();
		//The random strings linked to the edges.
		for (int i = 0; i < graphOfRandomStrings.length; i++) {
			for (int j = i; j < graphOfRandomStrings[i].size(); j++) {
				String randomString = getRandomString();
				graphOfRandomStrings[i].add(randomString);
				graphOfRandomStrings[j].add(randomString);
			}
		}
		return graphOfRandomStrings;
	}

	
	private String getRandomString() {
		StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < RANDOM_STRING_LENGTH) { // length of the random string.
            int index = rnd.nextInt(SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
	}
}
