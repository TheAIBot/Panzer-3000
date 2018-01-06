package graphics.Menu.Pages;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import graphics.GraphicsPanel;
import graphics.Menu.MenuController;

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
		page.addMouseMotionListener(control);
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
