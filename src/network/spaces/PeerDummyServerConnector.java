package network.spaces;

import java.io.IOException;
import java.util.ArrayList;

import org.jspace.SequentialSpace;

import engine.Input;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;

public class PeerDummyServerConnector extends SuperServerConnector {

	@Override
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initilizePrivateConnections(ClientInfo[] clientInfos, SequentialSpace startServerSpace)
			throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendWalls(ArrayList<Wall> walls) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendUpdate(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Input[] receiveUserInputs() throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnections() {
		// TODO Auto-generated method stub
		
	}

}
