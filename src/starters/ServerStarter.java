package starters;

import java.net.SocketException;
import java.net.UnknownHostException;

import connector.BasicServer;
import engine.GameEngine;

public class ServerStarter {
	
	public static void main(String[] args) throws UnknownHostException, SocketException {
		new BasicServer("aksdasl").startServer();
		//new GameEngine().startGame(1, new String[] {"Derp"});
	}
}
