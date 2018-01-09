package connector;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import Logger.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import engine.Client;
import engine.GameEngine;

public class BasicServer {
	public SpaceRepository repository;
	private SequentialSpace	clientConnectSpace;
	public String CLIENT_CONNECT_SPACE_NAME = "clientConnectSpace";
	private ServerInfo info;
	
	public BasicServer(String serverName) throws UnknownHostException {
		info = new ServerInfo();
		info.ipAddress = getIpAddress();
		info.name = serverName;
	}
	
	public static String getIpAddress() throws UnknownHostException {
		InetAddress ipAddr = InetAddress.getLocalHost();
		return ipAddr.getHostAddress();
	}
	
	public void startServer() throws UnknownHostException {
		
		clientConnectSpace = new SequentialSpace();
		repository = new SpaceRepository();
		repository.addGate("tcp://" + info.ipAddress + ":9001/?keep");
		repository.add(CLIENT_CONNECT_SPACE_NAME, clientConnectSpace);
		
		new Thread(() -> {
			try {
				clientConnectSpace.query(new ActualField("startGame"), new ActualField(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			Log.message("Started server");
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				Log.message("Server received a udp message");
				try {
					String message = BasicClient.bytesToString(receivePacket.getData());
					Log.message("Server received message: " + message);
					if (message.equals(BasicClient.BROADCAST_MESSAGE)) {
						byte[] sendData = info.toByteArray();
						for (InetAddress address : broadcastAddresses) {
							BasicClient.broadcastUDPMessage(socket, sendData, address, BasicClient.UDP_PORT_ANSWER);
						}
						Log.message("Server sent server information to all clients");
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
		
		
		new Thread(() -> {
			new GameEngine().startGame(2, usernames);
		}).start();
		
		clientConnectSpace.put(new ActualField("startGameAccepted"), new ActualField(1));
	}
}
