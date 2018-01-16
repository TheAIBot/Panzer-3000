package engine;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.jspace.SequentialSpace;

import logger.Log;
import network.spaces.ClientInfo;
import network.spaces.SuperServerConnector;

public class PeerGameEngine extends SuperGameEngine {

	long startTime;
	
	@Override
	public void startGame(int port, ClientInfo[] clientInfos, SuperServerConnector connection, SequentialSpace startServerSpace) throws Exception {
		connection.initializeServerConnection(port, clientInfos, startServerSpace);
	}

	public Object[] getUpdates(Input[] playerInputs) {

		startTime = System.currentTimeMillis();
		update(playerInputs);
		//Packing the tuples corresponding to the updated game information.
		//The lists are converted to to byte arrays, as the Client class expects this to be the case.
		
		Object[] tuple = new Object[4];
		tuple[0] = "updates";
		try {
			byte[] tankBytes 	= DeSerializer.toBytes(tanks);
			byte[] bulletBytes  = DeSerializer.toBytes(bullets);
			byte[] powerupBytes = DeSerializer.toBytes(powerups);
			
			tuple[1] = tankBytes;
			tuple[2] = bulletBytes;
			tuple[3] = powerupBytes;
			

			final long timePassed = System.currentTimeMillis() - startTime;
			final long timeToSleep = Math.max(0, (1000 / FPS) - timePassed);
			Thread.sleep(timeToSleep);
			
			return tuple;
		} catch (Exception e) {
			Log.exception(e);
		}	return tuple;
	}

	public Object[] getWalls() {
		byte[] wallBytes;
		Object[] tuple = new Object[2];
		tuple[0] = "walls";
		try {
			wallBytes = DeSerializer.toBytes(walls);
			tuple[1] = wallBytes;		
		} catch (IOException e) {
			Log.exception(e);
		}
		return tuple;
	}

	public void setRandomSeed(int randomSeed) {
		random.setSeed(randomSeed);
	}


}
