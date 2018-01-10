package connector;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import Logger.Log;
import engine.Client;
import engine.SecureRemoteSpace;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class BasicClient {
	ServerInfo serverInfo;
	SecureRemoteSpace serverConnection;
	MenuController menu;
	GraphicsPanel panel;
	public static final int MENU_HEIGHT = 0;
	public static final int MENU_WIDTH  = 0;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		BasicClient client = new BasicClient();
		

		ServerInfo curr = new ServerInfo();
		curr.name = "name";
		curr.ipAddress = "localhost";
		curr.clientsConnected = 1;
		
		client.joinGame(curr, "username1");
	}
	
	
	public void startClient() {

		menu = new MenuController("Panzer", MENU_HEIGHT, MENU_WIDTH);
		menu.showWindow();
		panel = GamePage.GetGraphicsPanel();
		Log.message("Created gui");
		
	}
	
	
	public ServerInfo[] queryServers()
	{
		return null;
	}
	
	
	public void joinGame(ServerInfo info, String username) throws UnknownHostException, IOException {
		//join the game -- connect to servers 
		serverConnection = new SecureRemoteSpace("tcp://" + info.ipAddress + ":9001/clientConnectSpace?keep");
		serverConnection.put(new ActualField(username));
		
		//listen for when to call startGame
		
		new Thread(() -> {
			try {
				serverConnection.query(new ActualField("startGameAccepted"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Client().startGame(info.ipAddress, username, menu, panel);
		}).start();
	}
	
	public void startGame(ServerInfo info) {
		serverConnection.put(new ActualField("startGame"), new ActualField(1));
	}
}
