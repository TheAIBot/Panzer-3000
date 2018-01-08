package connector;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import Logger.Log;
import engine.Client;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class BasicClient {
	ServerInfo serverInfo;
	RemoteSpace serverConnection;
	MenuController menu;
	GraphicsPanel panel;
	
	public void startClient() {

		menu = new MenuController("Panzer", 500, 500);
		menu.showWindow();
		panel = GamePage.GetGraphicsPanel();
		Log.message("Created gui");
		
	}
	
	
	public ServerInfo[] queryServers()
	{
		return null;
	}
	
	
	public void joinGame(ServerInfo info) throws UnknownHostException, IOException {
		//join the game -- connect to servers 
		serverConnection = new RemoteSpace("tcp://" + info.ipAddress + ":9001/clientConnectSpace?keep");
		
		//listen for when to call startGame
		
		new Thread(() -> {
			try {
				serverConnection.query(new ActualField("startGameAccepted"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Client().startGame(info.ipAddress, menu, panel);
		});
	}
	
	public void startGame(ServerInfo info) {
		serverConnection.put(new ActualField("startGame"), new ActualField(1));
	}
}
