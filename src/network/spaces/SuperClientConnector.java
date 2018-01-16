package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jspace.*;

import engine.*;
import engine.entities.Wall;

public abstract class SuperClientConnector {
	
	public abstract void connect(ServerInfo serverInfo, ClientInfo clientInfo) throws Exception;
	
	public abstract void initilizePrivateConnections(String ipaddress, int port) throws Exception;

	public abstract void sendUserInput(Input input) throws InterruptedException; 
	
	public abstract Object[] recieveUpdates() throws Exception;

	public abstract ArrayList<Wall> receiveWalls() throws Exception;
}
