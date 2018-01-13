package engine;

import java.util.ArrayList;

import graphics.GraphicsPanel;
import Menu.GUIControl;
import Menu.InputHandler;
import logger.Log;
import network.spaces.ClientConnector;
import network.spaces.ClientInfo;

public class Client {
	boolean hasPlayerWon = false;

	public void startGame(String ipaddress, int port, ClientInfo clientInfo, InputHandler inputHandler, GUIControl guiControl, GraphicsPanel panel) {
		try {
			Log.message("Starting client");
			ClientConnector connection = new ClientConnector();
			connection.connectToServer(ipaddress, port, clientInfo);
			guiControl.gameStarted();
			Log.message("Client connected");
			
			
			Object[] wallObjects = connection.receiveWalls();
			ArrayList<Wall> walls = DeSerializer.toList((byte[])wallObjects[0], Wall.class);
			panel.setWalls(walls);
			
			boolean firstUpdate = true;
			int clientCount = 0;
			while (true) {
				final Object[] updatedObjects = connection.recieveUpdates(); 
				final ArrayList<Tank>   tanks		= DeSerializer.toList((byte[])updatedObjects[0], Tank.class);
				final ArrayList<Bullet> bullets 	= DeSerializer.toList((byte[])updatedObjects[1], Bullet.class);
				final ArrayList<Powerup> powerups   = DeSerializer.toList((byte[])updatedObjects[2], Powerup.class);
				
				if (firstUpdate) {
					clientCount = tanks.size();
				}
				
				if (GameEngine.hasTankWonGame(tanks, clientCount)) {
					System.out.println("The game has been won!!!");
					hasPlayerWon = true;
					panel.setPlayerHasWon();
					
					Thread.sleep(2000);
					guiControl.gameEnded();
					return;
				}
				
				//Here the graphics needs to render the things seen above
				panel.setTanks(tanks);
				panel.setBullets(bullets);
				panel.setPowerups(powerups);
				panel.repaint();
				
				//finally send the inputs to the server.			
				connection.sendUserInput(inputHandler.getInput());
			}	
		} catch (Exception e) {
			Log.exception(e);
			guiControl.gameEnded();
		}
	}
}
