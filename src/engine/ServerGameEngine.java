package engine;

import org.jspace.SequentialSpace;

import logger.Log;
import network.spaces.DirectServerConnector;
import network.spaces.SuperServerConnector;

public class ServerGameEngine extends SuperGameEngine{

	@Override
	public void startGame(int port, int tankCount, String[] usernames, SuperServerConnector connection, SequentialSpace startServerSpace) {
		try {
			Log.message("Starting server");
			
			initializeGame(tankCount, usernames);
			
			this.connection = connection;			
			connection.initializeServerConnection(port, tankCount, usernames, startServerSpace);
			Log.message("Clients connected");
			

			// The server will send the initial information first, such that the clients
			// have something to display:

			connection.sendWalls(walls);
			((DirectServerConnector) connection).sendUpdates(tanks, bullets, powerups);
			Log.message("Sent first update");

			Thread.sleep(2000);

			// Then the main loop can begin:

			while (true) { // Game loop
				final long startTime = System.currentTimeMillis();
				Input[] userInputs = ((DirectServerConnector) connection).receiveUserInputs();
				// Log.message(userInputs[0].toString());
				// Log.message("Received inputs from clients");
				update(userInputs);

				// Log.message("Updated game");
				((DirectServerConnector) connection).sendUpdates(tanks, bullets, powerups);
				// Log.message("Sent game state update");
				if (hasTankWonGame(tanks, tankCount)) {
					// Victory!!!
					System.out.println("The game has been won!!!");
					break;
				}
				final long timePassed = System.currentTimeMillis() - startTime;
				final long timeToSleep = Math.max(0, (1000 / FPS) - timePassed);
				Thread.sleep(timeToSleep);
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}

}
