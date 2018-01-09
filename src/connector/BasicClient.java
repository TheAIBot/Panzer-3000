package connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
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
	ServerFoundListener listener;
	MenuController menu;
	
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
	
	public void searchForServers(ArrayList<InetAddress> broadcastAddresses) throws IOException
	{		
		try 
		{
			DatagramSocket socket = new DatagramSocket(UDP_PORT_ANSWER);
			socket.setReuseAddress(true);
			new Thread(() -> listenForServers(socket)).start();
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
	
	private void listenForServers(DatagramSocket socket)
	{
		try 
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
	
	
	public void joinGame(ServerInfo info, String username) throws UnknownHostException, IOException {
		//join the game -- connect to servers 
		serverConnection = new RemoteSpace("tcp://" + info.ipAddress + ":9001/clientConnectSpace?keep");
		serverConnection.put(new ActualField(username));
		
		//listen for when to call startGame
		
		new Thread(() -> {
			try {
				serverConnection.get(new ActualField("startGameAccepted"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Client().startGame(info.ipAddress, username, menu, GamePage.GetGraphicsPanel());
		}).start();
	}
	
	public void startGame(ServerInfo info) {
		serverConnection.put(new ActualField("startGame"), new ActualField(1));
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
