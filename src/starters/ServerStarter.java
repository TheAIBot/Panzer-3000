package starters;

import java.net.SocketException;
import java.net.UnknownHostException;

import engine.GameEngine;
import network.spaces.BasicServer;

public class ServerStarter {
	
	public static void main(String[] args) throws UnknownHostException, SocketException {
		new BasicServer("aksdasl").startServer();
		//new GameEngine().startGame(1, new String[] {"Derp"});
	}
}
