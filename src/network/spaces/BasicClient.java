package network.spaces;

import java.io.IOException;
import java.util.List;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Client;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;
import graphics.Menu.Pages.ServerSelectionPage;
import logger.Log;
import network.NetworkTools;

public class BasicClient {
	private ServerInfo serverInfo;
	private RemoteSpace serverConnection;
	private RemoteSpace serverStartSpace;
	private RemoteSpace serverStartAcceptedSpace;
	private MenuController menu;
	private Thread listenForGameStart;
	private String username;
	private boolean hasJoinedAGame = false;
	
	public BasicClient(MenuController menu) {
		this.menu = menu;
	}
	
	
	public void requestStartGame() throws InterruptedException {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
	
	public void joinGame(ServerInfo info, String username, final ServerSelectionPage page) throws UnknownHostException, IOException, InterruptedException {
		this.serverInfo = info;
		this.username = username;
		//join the game -- connect to servers 
		serverConnection 		 = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.CLIENT_CONNECT_SPACE_NAME + "?conn");
		serverStartSpace 		 = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_SPACE_NAME 		 + "?conn");
		serverStartAcceptedSpace = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_ACCEPTED_SPACE_NAME + "?conn");
		serverConnection.put(username, NetworkTools.getIpAddress());
		hasJoinedAGame = true;
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.get(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1));
			} catch (InterruptedException e) {
				Log.exception(e);
			}
			page.gameStarted();
			new Client().startGame(serverInfo.ipAddress, serverInfo.port, username, menu, GamePage.GetGraphicsPanel(), true);
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws InterruptedException, IOException {
		serverConnection.get(new ActualField(username), new ActualField(NetworkTools.getIpAddress()));
		listenForGameStart.interrupt();
		hasJoinedAGame = false;
		serverConnection.close();
		serverStartSpace.close();
		serverStartAcceptedSpace.close();
	}
	
	public boolean hasJoinedAGame() {
		return hasJoinedAGame;
	}
	
	public String[] getPlayerNames(ServerInfo info) throws InterruptedException, UnknownHostException, IOException {
		final List<Object[]> tuples = serverConnection.queryAll(new FormalField(String.class));
		
		final String[] playerNames = new String[tuples.size()];
		for (int i = 0; i < playerNames.length; i++) {
			playerNames[i] = (String) tuples.get(i)[0];
		}
		
		return playerNames;
	}
}
