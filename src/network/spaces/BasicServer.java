package network.spaces;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

<<<<<<< HEAD
import engine.DeSerializer;
import engine.GameEngine;
=======
import engine.Client;
import engine.PeerGameEngine;
import engine.ServerGameEngine;
import engine.SuperGameEngine;
>>>>>>> refs/remotes/origin/peer_to_peer
import logger.Log;
import network.NetworkTools;
import network.udp.ServerFinder;
import network.udp.UDPConnector;
import network.udp.UDPPacketListener;
import security.Crypto;
import security.SecureSpace;

public class BasicServer implements UDPPacketListener {
	private SpaceRepository repository;
	private SecureSpace	clientConnectSpace;
	private SequentialSpace startSpace;
	private SequentialSpace startAcceptedSpace;
	public  ServerInfo info;
	
	public static final String CLIENT_CONNECT_SPACE_NAME = "clientConnectSpace";
	public static final String START_SPACE_NAME = "startSpace";
	public static final String START_ACCEPTED_SPACE_NAME = "startAcceptedSpace";
	public static final String REQUEST_START_GAME = "startGame";
	public static final String START_GAME_ACCEPTED = "startGameAccepted";
	
	public BasicServer(String serverName) throws UnknownHostException, SocketException, NoSuchAlgorithmException, NoSuchProviderException {
		info = new ServerInfo();
		info.ipAddress = NetworkTools.getIpAddress();
		info.name = serverName;
		//chose a random port between 1025-2^15. Port starting at 1025
		//because the first 1024 ports are reserved
		info.port = (int)(Math.random() * Short.MAX_VALUE) + 1025;
	}
	
	public void startServer() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		
		final SequentialSpace sharedSpace = new SequentialSpace();
		clientConnectSpace = new SecureSpace(sharedSpace);
		info.publicKey = clientConnectSpace.getPublicKey();
		
		startSpace = new SequentialSpace();
		startAcceptedSpace = new SequentialSpace();
		repository         = new SpaceRepository();
		
		final String serverUri = "tcp://" + info.ipAddress + ":" + info.port  + "/?conn";
		repository.addGate(serverUri);
		
		repository.add(CLIENT_CONNECT_SPACE_NAME, sharedSpace);
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
			} catch (Exception e) {
				Log.exception(e);
			}
		}).start();
		
		UDPConnector.startListeningForBroadcasts(ServerFinder.UDP_PORT_ASK);
		UDPConnector.addUDPPacketListener(ServerFinder.UDP_PORT_ASK, this);
		UDPConnector.broadcastData(info.toByteArray(), ServerFinder.UDP_PORT_ANSWER);
	}
	
	private void startGame() throws Exception {
		final ArrayList<Object[]> users = clientConnectSpace.getAllWithIdentifier(new FormalField(String.class));
		
		final ClientInfo[] clientInfos = new ClientInfo[users.size()];
		for (int i = 0; i < clientInfos.length; i++) {
			clientInfos[i] = (ClientInfo)users.get(i)[0];
		}
		
		new Thread(() -> {
			new GameEngine().startGame(info.port, clientInfos, startAcceptedSpace);
		}).start();
	}

	@Override
	public void packetReceived(byte[] packetData) {
		try {
			final String message = (String)DeSerializer.decodeObjects(packetData)[0];
			if (message.equals(ServerFinder.BROADCAST_MESSAGE)) {
				UDPConnector.broadcastData(info.toByteArray(), ServerFinder.UDP_PORT_ANSWER);
			}
		} catch (Exception e) {
			return;
		}
	}
}
