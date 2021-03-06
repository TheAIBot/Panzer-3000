package network.spaces;

import java.net.URI;
import java.security.PublicKey;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Client;
import Menu.GUIControl;
import Menu.InputHandler;
import Menu.Pages.GamePage;
import logger.Log;
import network.CommunicationType;
import network.NetworkProtocol;
import network.NetworkTools;
import security.Crypto;
import security.SecureRemoteSpace;
import network.NetworkTools;

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
	
	
	public void requestStartGame() throws InterruptedException {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
	
	public void joinGame(ServerInfo info, String username, final GUIControl guiControl) throws Exception {
		this.serverInfo = info;
		this.clientInfo.username = username;
		this.clientInfo.salt =  Crypto.getRandomString(18);
		this.clientInfo.ipaddress = NetworkTools.getIpAddress();
		
		final URI serverConnectionURI    = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.CLIENT_CONNECT_SPACE_NAME, "conn");
		final URI serverStartSpaceURI    = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.START_SPACE_NAME         , "conn");
		final URI serverStartAcceptedURI = NetworkTools.createURI(NetworkProtocol.TCP, info.ipAddress, info.port, BasicServer.START_ACCEPTED_SPACE_NAME, "conn");
		
		this.serverConnection = new SecureRemoteSpace(serverConnectionURI, info.publicKey);
		this.serverStartSpace = new RemoteSpace(serverStartSpaceURI);
		this.serverStartAcceptedSpace = new RemoteSpace(serverStartAcceptedURI);
		
		serverConnection.putWithIdentifier(clientInfo.username, clientInfo);
		hasJoinedAGame = true;
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.get(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1));
				new Client().startGame(serverInfo, clientInfo, inputHandler, guiControl, GamePage.GetGraphicsPanel(), info.comType);
			} catch (InterruptedException e) {
				Log.exception(e);
			}
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws Exception
	{
		serverConnection.removeWithIdentifier(new ActualField(clientInfo.username));
		listenForGameStart.interrupt();
		hasJoinedAGame = false;
		serverConnection.close();
		serverStartSpace.close();
		serverStartAcceptedSpace.close();
	}
	
	public boolean hasJoinedAGame() {
		return hasJoinedAGame;
	}
	
	public int getPlayerCount(ServerInfo info) throws Exception {
		return serverConnection.tuplesWithIdentifierCount(new FormalField(String.class));
	}
}
