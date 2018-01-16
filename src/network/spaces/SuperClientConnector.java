package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.*;

import engine.*;
import logger.Log;

public abstract class SuperClientConnector {

	public RemoteSpace 	sharedSpace;
	public int 			connectionId;
	public int 			numberOfClients;
	public ClientInfo clientInfo;
	
	public abstract void connectToServer(ServerInfo serverInfo, ClientInfo clientInfo) throws UnknownHostException, IOException, InterruptedException;
	
	public abstract void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException;

	public abstract void sendUserInput(Input input) throws InterruptedException; 
	
	public abstract Object[] recieveUpdates() throws InterruptedException;

	public abstract Object[] receiveWalls() throws InterruptedException;
}
