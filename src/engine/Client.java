package engine;

import java.util.ArrayList;

import graphics.GraphicsPanel;
import Menu.GUIControl;
import Menu.InputHandler;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;
import logger.Log;
import network.spaces.ClientInfo;
import network.spaces.DirectClientConnector;
import network.spaces.PeerClientConnector;
import network.spaces.ServerInfo;
import network.spaces.SuperClientConnector;

public class Client {
	boolean hasPlayerWon = false;
	int numberOfClients = -1;

	public void startGame(ServerInfo serverInfo, ClientInfo clientInfo, InputHandler inputHandler, GUIControl guiControl, GraphicsPanel panel, boolean peerToPeer) {
		try {
			Log.message("Starting client");
			SuperClientConnector connection;
			if (peerToPeer) {
				connection = new PeerClientConnector();
			} else {
				connection = new DirectClientConnector();
			}
			connection.connectToServer(serverInfo, clientInfo);
			guiControl.gameStarted();
			Log.message("Client connected");
			
			
			Object[] wallObjects  = connection.receiveWalls();
			ArrayList<Wall> walls = DeSerializer.toList((byte[])wallObjects[1], Wall.class);
			panel.setWalls(walls);
			
			boolean firstUpdate = true;
			int clientCount = 0;
			while (true) {
				final Object[] updatedObjects = connection.recieveUpdates();
				final ArrayList<Tank>   tanks		= DeSerializer.toList((byte[])updatedObjects[1], Tank.class);
				final ArrayList<Bullet> bullets 	= DeSerializer.toList((byte[])updatedObjects[2], Bullet.class);
				final ArrayList<Powerup> powerups   = DeSerializer.toList((byte[])updatedObjects[3], Powerup.class);
				
				if (firstUpdate) {
					clientCount = tanks.size();
					firstUpdate = false;
				}
				
				//Here the graphics needs to render the things seen above
				
				panel.setTanks(tanks);
				panel.setBullets(bullets);
				panel.setPowerups(powerups);
				panel.repaint();
				
				if (GameEngine.hasTankWonGame(tanks, clientCount)) {
					hasPlayerWon = true;
					panel.setPlayerHasWon();
					panel.repaint();
					
					Thread.sleep(2000);
					guiControl.gameEnded();
					return;
				}
				
				//finally send the inputs to the server.			
				connection.sendUserInput(inputHandler.getInput());
			}	
		} catch (Exception e) {
			Log.exception(e);
			guiControl.gameEnded();
		}
	}
}
