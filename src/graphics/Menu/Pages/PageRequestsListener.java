package graphics.Menu.Pages;

import java.awt.Dimension;

public interface PageRequestsListener {
	public void back();
	
	public void switchPage(SuperPage switchTo);
	
	public void resize(Dimension dim);
	
	public void canResize(boolean canResize);
	
	public void setFullScreen();
	
	public void hideScreen();
	
	public void showScreen();
}
