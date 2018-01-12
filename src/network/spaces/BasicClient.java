package network.spaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Client;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;
import graphics.Menu.Pages.ServerSelectionPage;
import logger.Log;
import security.Crypto;
import security.SecureRemoteSpace;

public class BasicClient {
	private ServerInfo serverInfo;
	private SecureRemoteSpace serverConnection;
	private RemoteSpace serverStartSpace;
	private RemoteSpace serverStartAcceptedSpace;
	private MenuController menu;
	private Thread listenForGameStart;
	private String username;
	private String salt;
	private byte[] encryptedSalt;
	private boolean hasJoinedAGame = false;
	
	public BasicClient(MenuController menu) {
		this.menu = menu;
	}
	
	
	public void requestStartGame() {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
	
	public void joinGame(ServerInfo info, String username, final ServerSelectionPage page) throws Exception {
		this.serverInfo = info;
		this.username = username;
		if (salt == null) {
			this.salt = Crypto.getSaltString(18);
		}
		//join the game -- connect to servers 
		serverConnection = new SecureRemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.CLIENT_CONNECT_SPACE_NAME + "?conn", info.publicKey);
		serverStartSpace = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_SPACE_NAME + "?conn");
		serverStartAcceptedSpace = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_ACCEPTED_SPACE_NAME + "?conn");
		
		serverConnection.put(username, salt);
		hasJoinedAGame = true;
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.get(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1));
			} catch (InterruptedException e) {
				Log.exception(e);
			}
			page.gameStarted();
			
			new Client().startGame(serverInfo.ipAddress, serverInfo.port, username, salt, menu, GamePage.GetGraphicsPanel());
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws Exception
	{
		serverConnection.getEncryptedTuple(new ActualField(username));
		listenForGameStart.interrupt();
		hasJoinedAGame = false;
		serverConnection.close();
		serverStartSpace.close();
		serverStartAcceptedSpace.close();
	}
	
	public boolean hasJoinedAGame()
	{
		return hasJoinedAGame;
	}
	
	public int getPlayerCount(ServerInfo info) throws Exception {
		return serverConnection.size();
	}
}
