package starters;

<<<<<<< HEAD



import engine.Client;
=======
import java.net.URISyntaxException;

>>>>>>> refs/remotes/origin/udpbroadcast
import graphics.GraphicsPanel;
import graphics.Menu.MenuController;
import graphics.Menu.Pages.GamePage;

public class ClientStarter {
	
<<<<<<< HEAD
	public static void main(String[] args) {
		MenuController menu = new MenuController("Panzer", 500, 500);
=======
	public static void main(String[] args) throws URISyntaxException {		
		MenuController menu = new MenuController("Panzer", 800, 800);
>>>>>>> refs/remotes/origin/udpbroadcast
		menu.showWindow();
		GraphicsPanel panel = GamePage.GetGraphicsPanel();
		//new Client().startGame("localhost", "Derp", menu, panel);
	}
	
}