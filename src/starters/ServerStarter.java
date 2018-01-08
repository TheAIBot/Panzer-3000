package starters;

import connector.BasicServer;
import engine.GameEngine;

public class ServerStarter {
	
	public static void main(String[] args) {
		new BasicServer().startServer();
		//new GameEngine().startGame(2);
	}
}
