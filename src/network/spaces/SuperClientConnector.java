package network.spaces;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.*;

import engine.*;

public abstract class SuperClientConnector {
	
	public abstract void connect(ServerInfo serverInfo, ClientInfo clientInfo) throws Exception;
	
	public abstract void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException;

	public abstract void sendUserInput(Input input) throws InterruptedException; 
	
	public abstract Object[] recieveUpdates() throws InterruptedException;

	public abstract Object[] receiveWalls() throws InterruptedException;
}
