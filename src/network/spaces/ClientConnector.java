package network.spaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import org.jspace.*;

import engine.*;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;
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
	public void initilizePrivateConnections(String ipaddress, int port) throws Exception {
	}
	
	@Override
	public void sendUserInput(Input input) throws InterruptedException {
		privateServerConnections.put(input); 
	}
	
	@Override
	public ArrayList<Wall> receiveWalls() throws Exception {
		final Object[] walls = privateServerConnections.get(new FormalField(byte[].class));
		return DeSerializer.toList((byte[])walls[0], Wall.class);
	}
	
	@Override
	public Object[] recieveUpdates() throws Exception {
		final Object[] tuple = privateServerConnections.get(new FormalField(byte[].class), new FormalField(byte[].class), new FormalField(byte[].class));
		final ArrayList<Tank>   tanks		= DeSerializer.toList((byte[])tuple[0], Tank.class);
		final ArrayList<Bullet> bullets 	= DeSerializer.toList((byte[])tuple[1], Bullet.class);
		final ArrayList<Powerup> powerups   = DeSerializer.toList((byte[])tuple[2], Powerup.class);
		
		return new Object[] {tanks, bullets, powerups};
	}
}
