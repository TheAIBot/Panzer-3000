package starters;

import Menu.MenuController;

public class ClientStarter {
	
	public static void main(String[] args) {
		MenuController menu = new MenuController("Panzer", 510, 300);
		menu.showWindow();
		//new Client().startGame("localhost", "Derp", menu, panel);
	}
}