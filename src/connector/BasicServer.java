package connector;

import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import java.net.InetAddress;
import java.net.UnknownHostException;

import engine.Client;
import engine.GameEngine;

public class BasicServer {
	public SpaceRepository 	repository;
	SequentialSpace		clientConnectSpace;
	String CLIENT_CONNECT_SPACE_NAME = "updateSpace";
	String ipAddress;
	
	private String getIpAddress() throws UnknownHostException {
		InetAddress ipAddr = InetAddress.getLocalHost();
		return ipAddr.getHostAddress();
	}
	
	public void startServer() throws UnknownHostException {
		ipAddress = getIpAddress();
		
		clientConnectSpace = new SequentialSpace();
		repository.addGate("tcp://" + ipAddress + ":9001/?keep");
		repository.add(CLIENT_CONNECT_SPACE_NAME, clientConnectSpace);
		
		new Thread(() -> {
			try {
				clientConnectSpace.query(new ActualField("startGame"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startGame();
		});
		
	}
	
	public void startGame() {
		new Thread(() -> {
			new GameEngine().startGame(2, ipAddress);
		});
		
		clientConnectSpace.put(new ActualField("startGameAccepted"), new ActualField(1));
	}
}
