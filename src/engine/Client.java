package engine;

import java.util.ArrayList;

import Logger.Log;
import connector.ClientConnector;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;

public class Client {
	
	boolean hasPlayerWon = false;
	public void startGame(String ipaddress, String username, MenuController menu, GraphicsPanel panel) {
		try {
			Log.message("Starting client");
			ClientConnector connection = new ClientConnector();
			connection.connectToServer(ipaddress, username);
			Log.message("Client connected");
			
			
			Object[] wallObjects = connection.receiveWalls();
			ArrayList<Wall> walls = DeSerializer.toList((byte[])wallObjects[1], Wall.class);
			panel.setWalls(walls);
			
			while (!hasPlayerWon) {
				
				//The call is blocking, so it won't continue before the update is given
				Object[] updatedObjects 	= connection.recieveUpdates(); 
				ArrayList<Tank>   tanks		= DeSerializer.toList((byte[])updatedObjects[1], Tank.class);
				ArrayList<Bullet> bullets 	= DeSerializer.toList((byte[])updatedObjects[2], Bullet.class);
				
				if (GameEngine.hasTankWonGame(tanks, connection.numberOfClients)) {
					System.out.println("The game has been won!!!");
					hasPlayerWon = true;
					panel.setPlayerHasWon();
				}
				//Log.message("Received tanks and bullet updates");
				
				//Here the graphics needs to render the things seen above
				panel.setTanks(tanks);
				panel.setBullets(bullets);
				panel.repaint();

				//Create a new Input
				Input userInput = menu.getInput();
				//Log.message(userInput.toString());
				
				//finally send the inputs to the server.			
				connection.sendUserInput(userInput);
				//Log.message("Sent user input");
			}	
		} catch (Exception e) {
			Log.exception(e);
		}
		
	}

	private boolean hasTankWonGame(ArrayList<Tank> tanks) {
		return tanks.size() <= 1;
	}
}
