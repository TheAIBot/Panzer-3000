package starters;

import engine.Client;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class ClientStarter {
	
	public static void main(String[] args) {		
		MenuController menu = new MenuController("Panzer", 800, 800);
		menu.showWindow();
		GraphicsPanel panel = GamePage.GetGraphicsPanel();
		//new Client().startGame("localhost", "Derp", menu, panel);
	}
	
}