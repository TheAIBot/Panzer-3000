package connector;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import engine.Client;
import engine.GameEngine;

public class BasicServer {
	public SpaceRepository 	repository;
	SequentialSpace		clientConnectSpace;
	String CLIENT_CONNECT_SPACE_NAME = "clientConnectSpace";
	public String ipAddress;
	
	public static void main(String[] args) throws UnknownHostException {
		BasicServer server = new BasicServer();
		server.startServer();
	}
	
	private String getIpAddress() throws UnknownHostException {
		InetAddress ipAddr = InetAddress.getLocalHost();
		return ipAddr.getHostAddress();
	}
	
	public void startServer() throws UnknownHostException {
		ipAddress = getIpAddress();
		
		clientConnectSpace = new SequentialSpace();
		repository = new SpaceRepository();
		repository.addGate("tcp://" + ipAddress + ":9001/?keep");
		repository.add(CLIENT_CONNECT_SPACE_NAME, clientConnectSpace);
		
		new Thread(() -> {
			try {
				clientConnectSpace.query(new ActualField("startGame"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startGame();
		}).start();
		
	}
	
	public void startGame() {
		LinkedList<Object[]> users = clientConnectSpace.getAll(new FormalField(String.class));
		String[] usernames = new String[users.size()]; 
		int x = 0;
		for(Object[] temp : users) {
			usernames[x] = (String) temp[0];
			x++;
		}
		
		
		new Thread(() -> {
			new GameEngine().startGame(2, ipAddress, usernames);
		}).start();
		
		clientConnectSpace.put(new ActualField("startGameAccepted"), new ActualField(1));
	}
}
