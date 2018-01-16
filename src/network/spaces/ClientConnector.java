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

public class ClientConnector extends SuperClientConnector {
	public RemoteSpace 	privateServerConnections;
	
	@Override
	public void connect(ServerInfo info, ClientInfo clientInfo) throws Exception {
		//first connect to server through private connection
		final URI privateServerConnectionURI = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, clientInfo.salt, "keep");
		this.privateServerConnections = new RemoteSpace(privateServerConnectionURI);
		
		//then tell the server that the connection has been created
		privateServerConnections.put("connected");
	}
	
	@Override
	public void initilizePrivateConnections(String ipaddress, int port) throws UnknownHostException, IOException, InterruptedException {
	}
	
	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		privateServerConnections.put(input); 
	}
	
	@Override
	public Object[] receiveWalls() throws InterruptedException {
		return privateServerConnections.get(new FormalField(byte[].class));
	}
	
	@Override
	public Object[] recieveUpdates() throws InterruptedException {
		return privateServerConnections.get(new FormalField(byte[].class), new FormalField(byte[].class), new FormalField(byte[].class));
	}
}
