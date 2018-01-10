package connector;

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

import Logger.Log;
import engine.Client;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class BasicClient implements ServerFoundListener {
	ServerInfo serverInfo;
	RemoteSpace serverConnection;
	RemoteSpace serverStartSpace;
	RemoteSpace serverStartAcceptedSpace;
	ServerFoundListener listener;
	MenuController menu;
	Thread listenForGameStart;
	String username;
	
	public static final String BROADCAST_MESSAGE = "anyone there?";
	public static final int UDP_PORT_ASK = 3242;
	public static final int UDP_PORT_ANSWER = 3243;
	
	public BasicClient(MenuController menu) {
		this.menu = menu;
	}
	
	private static byte[] stringToBytes(String message) throws IOException
	{
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				out.writeUTF(message);
				return stream.toByteArray();
			}
		}
	}
	
	public static String bytesToString(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
			try (DataInputStream in = new DataInputStream(stream)) {
				ServerInfo info = new ServerInfo();
				return in.readUTF();
			}
		}
	}
	
	public void searchForServers() throws IOException {
		ArrayList<InetAddress> broadcastAddresses = getBroadcastAddresses();
		searchForServers(broadcastAddresses);
	}
	
	private void searchForServers(ArrayList<InetAddress> broadcastAddresses) throws IOException
	{		
		try 
		{
			DatagramSocket socket = new DatagramSocket();
			socket.setReuseAddress(true);
			for (InetAddress inetAddress : broadcastAddresses) {
				byte[] sendData = stringToBytes(BROADCAST_MESSAGE);
				broadcastUDPMessage(socket, sendData, inetAddress, UDP_PORT_ASK);
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public static void broadcastUDPMessage(DatagramSocket socket, byte[] message, InetAddress address, int port) throws IOException
	{
		socket.setBroadcast(true);
		socket.send(new DatagramPacket(message, message.length, address, port));
		Log.message("Client sent a udp message");
	}
	
	public void startListeningForServers()
	{
		new Thread(() -> listenForServers()).start();
	}
	
	private void listenForServers()
	{
		try (DatagramSocket socket = new DatagramSocket(UDP_PORT_ANSWER))
		{
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				try {
					final ServerInfo info = ServerInfo.toServerInfo(receivePacket.getData());
					listener.foundServer(info);
					Log.message("Found server: " + info.ipAddress);
				} catch (Exception e) {
					Log.exception(e);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public static ArrayList<InetAddress> getBroadcastAddresses() throws SocketException
	{
		final ArrayList<InetAddress> validBroadcastAddresses = new ArrayList<InetAddress>();
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (networkInterfaces.hasMoreElements()) {
	      for (InterfaceAddress ia : networkInterfaces.nextElement().getInterfaceAddresses()) {
	    	  InetAddress broadcastAddress = ia.getBroadcast();
	    	  if (broadcastAddress != null) {
				validBroadcastAddresses.add(broadcastAddress);
			}
	      }
	    }
	    return validBroadcastAddresses;
	}
	
	public static InetAddress getOwnIPAddress() throws SocketException
	{
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (networkInterfaces.hasMoreElements()) {
	    	NetworkInterface netInterface = networkInterfaces.nextElement();
	    	if (!netInterface.isVirtual() &&
	    		!netInterface.isPointToPoint() && 
	    		!netInterface.isLoopback() &&
	    		netInterface.isUp()) {
	  	      for (InterfaceAddress ia : netInterface.getInterfaceAddresses()) {
		    	  if (ia.getAddress() != null && ia.getAddress() instanceof Inet4Address) {
					return ia.getAddress();
				}
		      }
			}
	    }
	    return null;
	}
	
	
	public void joinGame(ServerInfo info, String username) throws UnknownHostException, IOException {
		this.serverInfo = info;
		this.username = username;
		//join the game -- connect to servers 
		serverConnection = new RemoteSpace("tcp://" + info.ipAddress + ":9001/" + BasicServer.CLIENT_CONNECT_SPACE_NAME + "?conn");
		serverStartSpace = new RemoteSpace("tcp://" + info.ipAddress + ":9001/" + BasicServer.START_SPACE_NAME + "?conn");
		serverStartAcceptedSpace = new RemoteSpace("tcp://" + info.ipAddress + ":9001/" + BasicServer.START_ACCEPTED_SPACE_NAME + "?conn");
		serverConnection.put(username);
		
		//listen for when to call startGame
		listenForGameStart = new Thread(() -> {
			try {
				serverStartAcceptedSpace.get(new ActualField(BasicServer.START_GAME_ACCEPTED), new ActualField(1), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Client().startGame(serverInfo.ipAddress, username, menu, GamePage.GetGraphicsPanel());
		});
		listenForGameStart.start();
	}
	
	public void leaveGame() throws InterruptedException
	{
		serverConnection.get(new ActualField(username));
		listenForGameStart.interrupt();
	}
	
	public String[] getPlayerNames(ServerInfo info) throws InterruptedException, UnknownHostException, IOException {
		List<Object[]> tuples = serverConnection.queryAll(new FormalField(String.class));
		String[] playerNames = new String[tuples.size()];
		for (int i = 0; i < playerNames.length; i++) {
			playerNames[i] = (String) tuples.get(i)[0];
		}
		//serverConnection.close();
		
		return playerNames;
	}
	
	public void startGame() {
		serverStartSpace.put(BasicServer.REQUEST_START_GAME, 1);
	}
	
	public void setServerFoaundLister(ServerFoundListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void foundServer(ServerInfo info) {
		Log.message("Client found server: " + info.ipAddress);
	}
}
