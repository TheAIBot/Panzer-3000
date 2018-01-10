package starters;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class ClientStarter {
	
	public static void main(String[] args) throws URISyntaxException {
		URI cake = new URI("tcp://127.0.0.1:1231/cake?keep");
		URI derp = new URI("socket://127.0.0.1:1231/cake?KEEP");
		InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 1234);
		System.out.println(socketAddress);
		
		if (cake.equals(derp)) {
			System.out.println("yay");
		}
		
		
		MenuController menu = new MenuController("Panzer", 800, 800);
		menu.showWindow();
		GraphicsPanel panel = GamePage.GetGraphicsPanel();
		//new Client().startGame("localhost", "Derp", menu, panel);
	}
}