package graphics.Menu.Pages;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Logger.Log;
import graphics.Menu.MenuController;

public abstract class SuperPage {
	protected JPanel page;
	protected SuperPage previousPage;
	private final MenuController controller;
	private PageRequestsListener listener;
	
	public SuperPage(MenuController control, PageRequestsListener listener)
	{
		this.controller = control;
		this.listener = listener;
	}
	
	public JPanel getPage()
	{
		if (isPageCreated()) {
			return page;
		}
		page = new JPanel();
		return createPage(controller);
	}
	
	public abstract JPanel createPage(MenuController control);
	
	public abstract void startPage();
	
	public abstract void closePage();
	
	public abstract boolean canShowPage();
	
	private boolean isPageCreated()
	{
		return page != null;
	}
	
	protected void backPage()
	{
		if (listener != null) {
			listener.back();
		}
		else
		{
			Log.message("PageRequestsListener is null");
		}
	}

	protected void switchPage(SuperPage switchTo)
	{
		listener.switchPage(switchTo);
	}
	
	protected void resize(Dimension dim)
	{
		listener.resize(dim);
	}
	
	protected void setFullScreen()
	{
		listener.setFullScreen();
	}
	
	protected void setResizeable(boolean canResize)
	{
		listener.canResize(canResize);
	}
	
	protected void hideScreen() {
		listener.hideScreen();
	}

	protected void showScreen() {
		listener.showScreen();
	}
}
