package starters;

import java.util.ArrayList;

import engine.Tank;
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class ClienterStarter {
	public static void main(String[] args) {
	 MenuController menu = new MenuController("Panzer", 500, 500);
	 menu.showWindow();
	 GraphicsPanel panel = GamePage.GetGraphicsPanel();
	 ArrayList<Tank> tanks = new ArrayList<Tank>();
	 tanks.add(new Tank(0.5, 0.5, 0.1, 0.2, 60 * (Math.PI / 180d), 0, 0));
	 panel.setTanks(tanks);
	}
}