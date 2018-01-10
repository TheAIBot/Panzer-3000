package network.spaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import engine.Client;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;
import graphics.Menu.Pages.ServerSelectionPage;
import logger.Log;
import network.NetworkTools;
import network.udp.UDPConnector;
import network.udp.UDPPacketListener;

public class BasicClient {
	ServerInfo serverInfo;
	RemoteSpace serverConnection;
	RemoteSpace serverStartSpace;
	RemoteSpace serverStartAcceptedSpace;
	MenuController menu;
	Thread listenForGameStart;
	String username;
	
	public BasicClient(MenuController menu) {
		this.menu = menu;
	}
	
	public void joinGame(ServerInfo info, String username, final ServerSelectionPage page) throws UnknownHostException, IOException {
		this.serverInfo = info;
		this.username = username;
		//join the game -- connect to servers 
		serverConnection = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.CLIENT_CONNECT_SPACE_NAME + "?conn");
		serverStartSpace = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_SPACE_NAME + "?conn");
		serverStartAcceptedSpace = new RemoteSpace("tcp://" + info.ipAddress + ":" + info.port + "/" + BasicServer.START_ACCEPTED_SPACE_NAME + "?conn");
		serverConnection.put(username);
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.query(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page.gameStarted();
			new Client().startGame(serverInfo.ipAddress, serverInfo.port, username, menu, GamePage.GetGraphicsPanel());
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws InterruptedException
	{
		serverConnection.get(new ActualField(username));
		listenForGameStart.interrupt();
	}
	
	public String[] getPlayerNames(ServerInfo info) throws InterruptedException, UnknownHostException, IOException {
		final List<Object[]> tuples = serverConnection.queryAll(new FormalField(String.class));
		
		final String[] playerNames = new String[tuples.size()];
		for (int i = 0; i < playerNames.length; i++) {
			playerNames[i] = (String) tuples.get(i)[0];
		}
		
		return playerNames;
	}
	
	public void startGame() {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
}
