package starters;

import connector.BasicClient;
import engine.Client;

public class ClientStarter {
	public static void main(String[] args) {
		new BasicClient().startClient();
		//new Client().startGame();
	}
}