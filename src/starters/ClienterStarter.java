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
	 tanks.add(new Tank(0.5, 0.5, 0.1, 0.1,  60 * (Math.PI / 180d), Math.toRadians( 30), 0));
	 tanks.add(new Tank(0.2, 0.5, 0.1, 0.1, 110 * (Math.PI / 180d), Math.toRadians( 60), 0));
	 tanks.add(new Tank(0.5, 0.2, 0.1, 0.2,  30 * (Math.PI / 180d), Math.toRadians(100), 0));
	 tanks.add(new Tank(0.8, 0.5, 0.1, 0.1, 240 * (Math.PI / 180d), Math.toRadians(150), 0));
	 tanks.add(new Tank(0.5, 0.7, 0.1, 0.1, 270 * (Math.PI / 180d), Math.toRadians(230), 0));
	 panel.setTanks(tanks);
	}
}