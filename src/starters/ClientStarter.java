package starters;

import connector.BasicClient;
import engine.Client;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class ClientStarter {
	public static void main(String[] args) {
		//new BasicClient().startClient();
		
		
		MenuController menu = new MenuController("Panzer", 500, 500);
		menu.showWindow();
		GraphicsPanel panel = GamePage.GetGraphicsPanel();
		
		new Client().startGame("localhost", "Derp", menu, panel);
	}
}