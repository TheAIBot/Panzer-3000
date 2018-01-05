package graphics.Menu.Pages;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import graphics.GraphicsPanel;

public class GamePage extends SuperPage {
	private static final GraphicsPanel graphicsPanel = new GraphicsPanel();

	public GamePage(PageRequestsListener listener) {
		super(listener);
	}
	
	public static GraphicsPanel GetGraphicsPanel()
	{
		return graphicsPanel;
	}

	@Override
	public JPanel createPage() {
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
