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
import network.CommunicationType;
import network.spaces.ClientConnector;
import network.spaces.ClientInfo;
import network.spaces.PeerClientConnector;
import network.spaces.ServerInfo;
import network.spaces.SuperClientConnector;

public class Client {
	@SuppressWarnings("unchecked")
	public void startGame(ServerInfo serverInfo, ClientInfo clientInfo, InputHandler inputHandler, GUIControl guiControl, GraphicsPanel panel, CommunicationType comType) throws InterruptedException {
		try {
			final SuperClientConnector connection = (comType == CommunicationType.P2P) ? new PeerClientConnector() : new ClientConnector();
			
			Log.message("Starting client");
			connection.connect(serverInfo, clientInfo);
			connection.initilizePrivateConnections(serverInfo.ipAddress, serverInfo.port);
			guiControl.gameStarted();
			Log.message("Client connected");
			
			
			ArrayList<Wall> walls  = connection.receiveWalls();
			panel.setWalls(walls);
			
			panel.startRendering();
			
			boolean firstUpdate = true;
			int clientCount = 0;
			while (true) {
				final Object[] updatedObjects = connection.recieveUpdates();
				final ArrayList<Tank>    tanks		= (ArrayList<Tank>)updatedObjects[0];
				final ArrayList<Bullet>  bullets 	= (ArrayList<Bullet>)updatedObjects[1];
				final ArrayList<Powerup> powerups   = (ArrayList<Powerup>)updatedObjects[2];
				
				if (firstUpdate) {
					clientCount = tanks.size();
					firstUpdate = false;
				}
				
				//Here the graphics needs to render the things seen above
				
				panel.setTanks(tanks);
				panel.setBullets(bullets);
				panel.setPowerups(powerups);
				
				if (GameEngine.hasTankWonGame(tanks, clientCount)) {
					panel.setPlayerHasWon();
					Thread.sleep(3000);
					break;
				}
				
				//finally send the inputs to the server.			
				connection.sendUserInput(inputHandler.getInput().copy());
			}	
		} catch (Exception e) {
			Log.exception(e);
		}
		
		panel.stopRendering();
		panel.resetGraphics();
		guiControl.gameEnded();
	}
}
