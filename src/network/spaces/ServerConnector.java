package network.spaces;

import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.*;

import org.jspace.*;

import engine.*;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;
import network.NetworkProtocol;
import network.NetworkTools;
import security.SecureSpace;

public class ServerConnector {	
	private SpaceRepository repository;
	private ClientInfo[] clientInfos;
	private SequentialSpace[] clientSpaces;
	
	
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws InterruptedException, UnknownHostException, SocketException, URISyntaxException {
		this.clientInfos = clientInfos;		
		this.repository = new SpaceRepository();
		this.clientSpaces = new SequentialSpace[clientInfos.length];
		
		//all clients will communicate with the server through this gate
		final URI gateURI = NetworkTools.createURI(NetworkProtocol.TCP, NetworkTools.getIpAddress(), port, "", "keep");
		repository.addGate(gateURI);
		
		//add a private space for each client
		for (int i = 0; i < clientSpaces.length; i++) {
			clientSpaces[i] = new SequentialSpace();
			repository.add(clientInfos[i].salt, clientSpaces[i]);
		}
		
		//tell the clients that they can now connect
		for (int i = 0; i < clientInfos.length; i++) {
			startServerSpace.put(BasicServer.START_GAME_ACCEPTED, 1);	
		}
		
		//waits for all clients to connect
		for (int id = 0; id < clientSpaces.length; id++) {				
				clientSpaces[id].get(new ActualField("connected"));
		}
	}
	
	public void sendWalls(ArrayList<Wall> walls) throws IOException, InterruptedException {
		for (int i = 0; i < clientInfos.length; i++) {
			final byte[] wallBytes = DeSerializer.toBytes(walls);
			clientSpaces[i].put(wallBytes);
		}
	}
	
	public void sendUpdates(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups) throws InterruptedException, IOException {
		for (int i = 0; i < clientInfos.length; i++) {
			final byte[] tankBytes = DeSerializer.toBytes(tanks);
			final byte[] bulletBytes = DeSerializer.toBytes(bullets);
			final byte[] powerupBytes = DeSerializer.toBytes(powerups);
			clientSpaces[i].put(tankBytes, bulletBytes, powerupBytes);
		}
	}
	
	
	public Input[] reciveUserInputs() throws InterruptedException, IOException {
		Input[] recievedInputs = new Input[clientInfos.length];
		for (int i = 0; i < clientInfos.length; i++) {
			final Object[] tuple = clientSpaces[i].get(new FormalField(Input.class));
			final Input input = (Input) tuple[0];
			
			input.id = i;
			recievedInputs[input.id] = input;
		}
		
		return recievedInputs;
	}
}
