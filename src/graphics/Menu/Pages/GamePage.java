package graphics.Menu.Pages;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;

import graphics.GraphicsPanel;

public class GamePage extends SuperPage {
	private static final GraphicsPanel graphicsPanel = new GraphicsPanel();

	public GamePage(PageRequestsListener listener) {
		super(listener);
	}
	
	public static JPanel GetGraphicsPanel()
	{
		return graphicsPanel;
	}

	@Override
	public JPanel createPage() {
		page.add(graphicsPanel, createConstraint(0, 0, 1, 1, GridBagConstraints.CENTER, false, GridBagConstraints.BOTH));
		return page;
	}
	
	private GridBagConstraints createConstraint(int gridX, int gridY, int gridWidth, int gridHeight, int anchor, boolean extraSpace, int fill) {
		GridBagConstraints contraint = new GridBagConstraints();
		contraint.gridx = gridX;
		contraint.gridy = gridY;
		if (!extraSpace) {
			contraint.weightx = 0;
			contraint.weighty = 0;
		} else {
			contraint.weightx = 1;
			contraint.weighty = 1;
		}
		contraint.fill = fill;
		contraint.insets = new Insets(5, 5, 5, 5);
		contraint.gridwidth = gridWidth;
		contraint.gridheight = gridHeight;
		contraint.anchor = anchor;
		return contraint;
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
