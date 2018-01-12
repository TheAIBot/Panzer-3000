package network.spaces;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import engine.Client;
import engine.GameEngine;
import logger.Log;
import network.NetworkTools;
import network.udp.ServerFinder;
import network.udp.UDPConnector;
import network.udp.UDPPacketListener;
import security.Crypto;

public class BasicServer implements UDPPacketListener {
	private SpaceRepository repository;
	private SequentialSpace	clientConnectSpace;
	private SequentialSpace startSpace;
	private SequentialSpace startAcceptedSpace;
	private ServerInfo info;
	private KeyPair keyPair;
	
	public static final String CLIENT_CONNECT_SPACE_NAME = "clientConnectSpace";
	public static final String START_SPACE_NAME = "startSpace";
	public static final String START_ACCEPTED_SPACE_NAME = "startAcceptedSpace";
	public static final String REQUEST_START_GAME = "startGame";
	public static final String START_GAME_ACCEPTED = "startGameAccepted";
	
	public BasicServer(String serverName) throws UnknownHostException, SocketException, NoSuchAlgorithmException, NoSuchProviderException {
		info = new ServerInfo();
		keyPair = Crypto.getPair();
		info.publicKey = keyPair.getPublic();
		info.ipAddress = NetworkTools.getIpAddress();
		info.name = serverName;
		//chose a random port between 1025-2^15. Port starting at 1025
		//because the first 1024 first 1024 ports are reserved
		info.port = (int)(Math.random() * Short.MAX_VALUE) + 1025;
	}
	
	public void startServer() throws IOException {
		
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
			try {
				startGame();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		UDPConnector.startListeningForBroadcasts(ServerFinder.UDP_PORT_ASK);
		UDPConnector.addUDPPacketListener(ServerFinder.UDP_PORT_ASK, this);
		UDPConnector.broadcastData(info.toByteArray(), ServerFinder.UDP_PORT_ANSWER);
	}
	
	public void startGame() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		final LinkedList<Object[]> users = clientConnectSpace.getAll(new FormalField(String.class), new FormalField(byte[].class));
		
		final String[] usernames = new String[users.size()]; 
		final String[] salts = new String[users.size()]; 
		for (int i = 0; i < usernames.length; i++) {
			usernames[i] = (String) users.get(i)[0];
			byte[] encSalt = (byte[]) users.get(i)[1];
			salts[i] = Crypto.decrypt(encSalt, keyPair.getPrivate());
		}
		
		new Thread(() -> {
			new GameEngine().startGame(info.port , usernames.length, usernames, salts, startAcceptedSpace);
		}).start();
	}

	@Override
	public void packetReceived(byte[] packetData) {
		try {
			final String message = NetworkTools.bytesToString(packetData);
			if (message.equals(ServerFinder.BROADCAST_MESSAGE)) {
				UDPConnector.broadcastData(info.toByteArray(), ServerFinder.UDP_PORT_ANSWER);
			}
		} catch (Exception e) {
			return;
		}
	}
}