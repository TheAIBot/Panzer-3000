package starters;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import network.spaces.BasicServer;

public class ServerStarter {
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		new BasicServer("aksdasl").startServer();
		//new GameEngine().startGame(1, new String[] {"Derp"});
	}
}
