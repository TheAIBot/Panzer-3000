package starters;

import java.net.UnknownHostException;

import connector.BasicServer;
import engine.GameEngine;

public class ServerStarter {
	
	public static void main(String[] args) throws UnknownHostException {
		new BasicServer().startServer();
		//new GameEngine().startGame(2);
	}
}
