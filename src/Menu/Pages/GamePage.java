package Menu.Pages;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import graphics.GraphicsPanel;
import Menu.MenuController;

public class GamePage extends SuperPage {
	private static final GraphicsPanel graphicsPanel = new GraphicsPanel();

	public GamePage(MenuController control, PageRequestsListener listener) {
		super(control, listener);
	}
	
	public static GraphicsPanel GetGraphicsPanel()
	{
		return graphicsPanel;
	}

	@Override
	public JPanel createPage(MenuController control) {
		page.setLayout(new GridBagLayout());
		page.add(graphicsPanel);
		return page;
	}

	@Override
	public void startPage() {
		setFullScreen();
		
	}

	@Override
	public void closePage() {
	}

	@Override
	public boolean canShowPage() {
		return true;
	}

}
