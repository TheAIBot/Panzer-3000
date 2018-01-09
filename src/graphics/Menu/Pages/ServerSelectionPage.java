package graphics.Menu.Pages;

import javax.swing.JPanel;

import connector.BasicClient;
import connector.BasicServer;
import connector.ServerFoundListener;
import connector.ServerInfo;
import graphics.Menu.MenuController;

public class ServerSelectionPage extends SuperPage implements ServerFoundListener {
	BasicClient client = new BasicClient(controller);
	BasicServer server;
	ServerList serverListPage;

	public ServerSelectionPage(MenuController control, PageRequestsListener listener) {
		super(control, listener);
		client.setServerFoaundLister(this);
	}

	@Override
	public JPanel createPage(MenuController control) {
		setResizeable(true);
		serverListPage = new ServerList(this);
		page = serverListPage;
		return page;
	}

	@Override
	public void startPage() {	
		client.
	}

	@Override
	public void closePage() {
		
	}

	@Override
	public boolean canShowPage() {
		return true;
	}

	@Override
	public void foundServer(ServerInfo info) {
		if (serverListPage != null) {
			serverListPage.addServer(info);
		}
	}

}
