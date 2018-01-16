package network.spaces;

import java.io.IOException;
import java.util.*;

import org.jspace.*;

import engine.*;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;

public abstract class SuperServerConnector {	
	public SpaceRepository 	repository;
	protected ClientInfo[] clientInfos;
	 
	public abstract void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws Exception;
	
	public abstract void initilizePrivateConnections(ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws InterruptedException;

	public abstract void sendWalls(ArrayList<Wall> walls) throws Exception;
	
	public abstract void sendUpdate(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups) throws Exception;
	
	public abstract Input[] receiveUserInputs() throws InterruptedException, IOException;
	
	public abstract void closeConnections();
}
