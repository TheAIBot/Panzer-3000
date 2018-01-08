package connector;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import engine.Client;

public class BasicClient {
	ServerInfo serverInfo;
	RemoteSpace serverConnection;
	
	public ServerInfo[] queryServers()
	{
		return null;
	}
	
	
	public void joinGame(ServerInfo info) {
		//join the game
		
		
		//listen for when to call startGame
		
		new Thread(() -> {
			serverConnection.query(new ActualField("startGame"), new ActualField(1));
			new Client().startGame(info.ipaddress);
		});
	}
	
	public void startGame(ServerInfo info) {
		serverConnection.put(new ActualField("startGame"), new ActualField(1));
	}
}
