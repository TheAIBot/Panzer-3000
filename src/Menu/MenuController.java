package Menu;


import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Stack;

import javax.swing.JFrame;

import engine.Input;
import Menu.Pages.GamePage;
import Menu.Pages.PageRequestsListener;
import Menu.Pages.ServerSelectionPage;
import Menu.Pages.SuperPage;
import logger.Log;

public class MenuController implements PageRequestsListener, KeyListener, MouseListener, MouseMotionListener, InputHandler, GUIControl {
	private final JFrame mainMenu;
	private final ServerSelectionPage MAIN_PAGE = new ServerSelectionPage(this, this);
	private final GamePage gamePage = new GamePage(this, this);
	private SuperPage currentPage;
	private final Stack<SuperPage> previousPages = new Stack<SuperPage>();
	private final Input input = new Input();
	
	public MenuController(String windowName, int startWidth, int startHeight) {
		mainMenu = new JFrame(windowName);
		mainMenu.setSize(startWidth, startHeight);
		mainMenu.setLocationRelativeTo(null);
		mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainMenu.addWindowListener(new WindowAdapter() {
			@Override
	        public void windowClosing(WindowEvent e) {
	            super.windowClosing(e);
	            currentPage.closePage();
	            //AudioManager.closeBackgroundMusic();
	        }
		});
		
		mainMenu.addKeyListener(this);
		mainMenu.addMouseListener(this);
		mainMenu.addMouseMotionListener(this);
	}
	
	public void showWindow()
	{
		switchPage(MAIN_PAGE, false);
	}

	@Override
	public void back() {
		if (!previousPages.isEmpty()) {
			switchPage(previousPages.pop(), false);
		}
		else {
			Log.message("Tried to go back a page when there is no previous page to go back to");
		}
	}
	
	@Override
	public void switchPage(SuperPage toSwitchTo)
	{
		switchPage(toSwitchTo, true);
	}
	
	private void switchPage(SuperPage toSwitchTo, boolean addPreviousPage)
	{
		if (toSwitchTo.canShowPage()) {
			if (addPreviousPage) {
				previousPages.add(currentPage);
			}
			if (currentPage != null) {
				currentPage.closePage();
			}
			
			currentPage = toSwitchTo;
			mainMenu.getContentPane().removeAll();
			mainMenu.add(currentPage.getPage());
			mainMenu.repaint();
			mainMenu.setVisible(true);
			//KeyListener won't work without this line
			mainMenu.requestFocus();
			
			currentPage.startPage();
		}
	}

	@Override
	public void resize(Dimension dim) {
		mainMenu.setSize(dim.width, dim.height);
	}

	@Override
	public void canResize(boolean canResize) {
		mainMenu.setResizable(canResize);
		
	}

	@Override
	public void setFullScreen() {
		mainMenu.setExtendedState(JFrame.MAXIMIZED_BOTH); 	
	}
	
	@Override
	public void exitFullScreen() {
		mainMenu.setExtendedState(JFrame.NORMAL);
	}

	@Override
	public void hideScreen() {
		mainMenu.setVisible(false);
	}

	@Override
	public void showScreen() {
		mainMenu.setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'w':
			input.w = true;
			break;
		case 'a':
			input.a = true;
			break;
		case 's':
			input.s = true;
			break;
		case 'd':
			input.d = true;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'w':
			input.w = false;
			break;
		case 'a':
			input.a = false;
			break;
		case 's':
			input.s = false;
			break;
		case 'd':
			input.d = false;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public Input getInput()
	{
		return input;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			input.click = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			input.click = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateInputMousePos(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateInputMousePos(e);
	}
	
	private void updateInputMousePos(MouseEvent e) {
		final int thisWidth = currentPage.getPage().getWidth();
		final int eWidth = GamePage.GetGraphicsPanel().getWidth();
		int k = (thisWidth - eWidth) / 2;
		final double x = ((double)e.getX() - k) / eWidth;
		final double y = (double)e.getY() / e.getComponent().getHeight();
		if (Double.isInfinite(x) || Double.isNaN(x) ||
			Double.isInfinite(y) || Double.isNaN(y)) {
			input.x = 0;
			input.y = 0;
		}
		else {
			input.x = x;
			input.y = y;
		}
	}

	@Override
	public void gameStarted() {
		switchPage(gamePage, false);
	}

	@Override
	public void gameEnded() {
		switchPage(MAIN_PAGE, false);
	}
}
