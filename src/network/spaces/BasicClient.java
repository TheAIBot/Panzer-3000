package network.spaces;

import java.net.URI;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;

import engine.Client;
import Menu.GUIControl;
import Menu.InputHandler;
import Menu.Pages.GamePage;
import logger.Log;
import network.NetworkProtocol;
import network.NetworkTools;
import security.Crypto;
import security.SecureRemoteSpace;

public class BasicClient {
	private ServerInfo serverInfo;
	private SecureRemoteSpace serverConnection;
	private RemoteSpace serverStartSpace;
	private RemoteSpace serverStartAcceptedSpace;
	private InputHandler inputHandler;
	private Thread listenForGameStart;
	private boolean hasJoinedAGame = false;
	private ClientInfo clientInfo = new ClientInfo();
	
	public BasicClient(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
	
	
	public void requestStartGame() {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
	
	public void joinGame(ServerInfo info, String username, final GUIControl guiControl) throws Exception {
		this.serverInfo = info;
		this.clientInfo.username = username;
		this.clientInfo.salt =  Crypto.getSaltString(18);
		
		final URI serverConnectionURI    = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.CLIENT_CONNECT_SPACE_NAME, "conn");
		final URI serverStartSpaceURI    = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.START_SPACE_NAME         , "conn");
		final URI serverStartAcceptedURI = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.START_ACCEPTED_SPACE_NAME, "conn");
		
		this.serverConnection = new SecureRemoteSpace(serverConnectionURI, info.publicKey);
		this.serverStartSpace = new RemoteSpace(serverStartSpaceURI);
		this.serverStartAcceptedSpace = new RemoteSpace(serverStartAcceptedURI);
		
		serverConnection.put(clientInfo);
		hasJoinedAGame = true;
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.get(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1));
			} catch (InterruptedException e) {
				Log.exception(e);
			}
			
			new Client().startGame(serverInfo.ipAddress, serverInfo.port, clientInfo, inputHandler, guiControl, GamePage.GetGraphicsPanel());
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws Exception
	{
		serverConnection.getEncryptedTuple(new ActualField(clientInfo));
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
