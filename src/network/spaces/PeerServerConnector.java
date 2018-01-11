package network.spaces;

import java.util.Random;

import org.jspace.SequentialSpace;

public class PeerServerConnector extends SuperServerConnector{

	public final int RANDOM_STRING_LENGTH = 12;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initilizePrivateConnections(SequentialSpace startServerSpace) throws InterruptedException {
		//It creates the different tuples with private id's:
		String[][] privateIDTuples = createPrivateIDs(numClients);
	}

	private String[][] createPrivateIDs(int numClients) {
		//A complete graph is made:
		String[][] graphOfRandomStrings = new String[numClients][numClients-1];
		Random random = new Random();
		//The random strings linked to the edges.
		for (int i = 0; i < graphOfRandomStrings.length; i++) {
			for (int j = 0; j < graphOfRandomStrings[i].length; j++) {
				String randomString = "";				
				for (int k = 0; k < RANDOM_STRING_LENGTH; k++) {
					randomString += (char) (random.nextInt() + 1);
				}
				
			}
		}
		
	}

}
