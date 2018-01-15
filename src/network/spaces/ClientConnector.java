package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import org.jspace.*;

import engine.*;
import logger.Log;
import network.NetworkProtocol;
import network.NetworkTools;
import security.SecureRemoteSpace;

public class ClientConnector {
	public RemoteSpace 	privateServerConnections;
	
	public void connectToServer(ServerInfo info, ClientInfo clientInfo) throws Exception {
		//first connect to server through private connection
		final URI privateServerConnectionURI = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, clientInfo.salt, "keep");
		this.privateServerConnections = new RemoteSpace(privateServerConnectionURI);
		
		//then tell the server that the connection has been created
		privateServerConnections.put("connected");
	}
	
	public Object[] receiveWalls() throws Exception {
		return privateServerConnections.get(new FormalField(byte[].class));
	}
	
	public Object[] recieveUpdates() throws Exception {
		return privateServerConnections.get(new FormalField(byte[].class), new FormalField(byte[].class), new FormalField(byte[].class));
	}	
	
	public void sendUserInput(Input input) throws Exception {
		privateServerConnections.put(input); 
	}
}
