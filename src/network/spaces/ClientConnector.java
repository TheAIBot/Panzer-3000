package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.*;

import engine.*;
import logger.Log;
import network.NetworkProtocol;
import network.NetworkTools;

public class ClientConnector {
	public RemoteSpace 	privateServerConnections;
	
	public void connectToServer(String ipAddress, int port, ClientInfo clientInfo) throws UnknownHostException, IOException, InterruptedException, URISyntaxException {
		//first connect to server through private connection
		final URI privateServerConnectionURI = NetworkTools.createURI(NetworkProtocol.TCP, ipAddress, port, clientInfo.salt, "keep");
		this.privateServerConnections = new RemoteSpace(privateServerConnectionURI);
		
		//then tell the server that the connection has been created
		privateServerConnections.put("connected");
	}
	
	public Object[] receiveWalls() throws InterruptedException {
		return privateServerConnections.get(new FormalField(byte[].class));
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return privateServerConnections.get(new FormalField(byte[].class), new FormalField(byte[].class), new FormalField(byte[].class));
	}	
	
	public void sendUserInput(Input input) {
		privateServerConnections.put(input);		
	}
}
