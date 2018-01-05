package engine;

import java.util.List;

import connector.ClientConnector;

public class Client {
	
	ClientConnector connection;
	
	public static void main(String[] args) {
		new Client().startGame();
	}
	
	public void startGame() {
		connection = new ClientConnector();
		connection.connectToServer();
		
		
		while (true) {
			
			//The call is blocking, so it won't continue before the update is given
			Object[] updatedObjects = connection.recieveUpdates(); 
			List<Tank> tanks 		= connection.unpackTanks(updatedObjects);
			List<Bullet> bullets 	= connection.unpackBullets(updatedObjects);

			//Create a new Input
			
			Input userInput = new Input(); //TODO actually set the user input
			
			//Here the graphics needs to render the things seen above
			
			
			
			//finally send the inputs to the server.			
			connection.sendUserInput(userInput);			
		}
	}
}
