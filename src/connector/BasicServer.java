package connector;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import Logger.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;

import engine.Client;
import engine.GameEngine;

public class BasicServer {
	public SpaceRepository repository;
	private SequentialSpace	clientConnectSpace;
	private SequentialSpace startSpace;
	private SequentialSpace startAcceptedSpace;
	public static final String CLIENT_CONNECT_SPACE_NAME = "clientConnectSpace";
	public static final String START_SPACE_NAME = "startSpace";
	public static final String START_ACCEPTED_SPACE_NAME = "startAcceptedSpace";
	public static final String REQUEST_START_GAME = "startGame";
	public static final String START_GAME_ACCEPTED = "startGameAccepted";
	private ServerInfo info;
	
	public BasicServer(String serverName) throws UnknownHostException, SocketException {
		info = new ServerInfo();
		info.ipAddress = getIpAddress();
		info.name = serverName;
		//chose a random port between 1025-2^15. Port starting at 1025
		//because the first 1024 first 1024 ports are reserved
		info.port = (int)(Math.random() * Short.MAX_VALUE) + 1025;
	}
	
	public static String getIpAddress() throws UnknownHostException, SocketException {
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (networkInterfaces.hasMoreElements()) {
	    	NetworkInterface netInterface = networkInterfaces.nextElement();
	    	if (!netInterface.isVirtual() &&
	    		!netInterface.isPointToPoint() && 
	    		!netInterface.isLoopback() &&
	    		netInterface.isUp()) {
				for (InterfaceAddress ia : netInterface.getInterfaceAddresses()) {
					if (ia.getAddress() != null && ia.getAddress() instanceof Inet4Address) {
						return ia.getAddress().getHostAddress();
					}
				}
			}
	    }
	    throw new UnknownHostException("Failed to find this computers ipaddress");
	}
	
	public void startServer() throws UnknownHostException {
		
		clientConnectSpace = new SequentialSpace();
		startSpace = new SequentialSpace();
		startAcceptedSpace = new SequentialSpace();
		repository = new SpaceRepository();
		final String serverUri = "tcp://" + info.ipAddress + ":" + info.port  + "/?conn";
		repository.addGate(serverUri);
		repository.add(CLIENT_CONNECT_SPACE_NAME, clientConnectSpace);
		repository.add(START_SPACE_NAME, startSpace);
		repository.add(START_ACCEPTED_SPACE_NAME, startAcceptedSpace);
		
		new Thread(() -> {
			try {
				startSpace.get(new ActualField(REQUEST_START_GAME), new ActualField(1));
			} catch (InterruptedException e) {
				Log.exception(e);
			}
			repository.closeGate(serverUri);
			startGame();
		}).start();
		
		new Thread(() -> receiveBroadcasts()).start();
	}
	
	private void receiveBroadcasts()
	{
		try (DatagramSocket socket = new DatagramSocket(BasicClient.UDP_PORT_ASK))
		{
			socket.setReuseAddress(true);
			final ArrayList<InetAddress> broadcastAddresses = BasicClient.getBroadcastAddresses();
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				//Log.message("Server received a udp message");
				try {
					String message = BasicClient.bytesToString(receivePacket.getData());
					//Log.message("Server received message: " + message);
					if (message.equals(BasicClient.BROADCAST_MESSAGE)) {
						byte[] sendData = info.toByteArray();
						for (InetAddress address : broadcastAddresses) {
							BasicClient.broadcastUDPMessage(socket, sendData, address, BasicClient.UDP_PORT_ANSWER);
						}
						//Log.message("Server sent server information to all clients");
					}
				} catch (Exception e) {	
					Log.exception(e);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public void startGame() {
		LinkedList<Object[]> users = clientConnectSpace.getAll(new FormalField(String.class));
		String[] usernames = new String[users.size()]; 
		int x = 0;
		for(Object[] temp : users) {
			usernames[x] = (String) temp[0];
			x++;
		}
		
		Log.message("starting server asdljasldjahdkjashdaskjdhaskjdhaskjdsak");
		
		new Thread(() -> {
			new GameEngine().startGame(info.port , usernames.length, usernames, startAcceptedSpace);
		}).start();
	}
}
