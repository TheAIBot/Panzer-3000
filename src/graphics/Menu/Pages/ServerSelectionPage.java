package graphics.Menu.Pages;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JPanel;

import Logger.Log;
import connector.BasicClient;
import connector.BasicServer;
import connector.ServerFoundListener;
import connector.ServerInfo;
import graphics.Menu.MenuController;

public class ServerSelectionPage extends SuperPage implements ServerFoundListener {
	GamePage gamePage = new GamePage(controller, controller);
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
		try {
			client.searchForServers();
		} catch (IOException e) {
			Log.exception(e);
		}
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
	
	public String[] getPlayerNames(ServerInfo info) throws UnknownHostException, InterruptedException, IOException {
		return client.getPlayerNames(info);
	}
	
	public void joinGame(ServerInfo info, String username) throws UnknownHostException, IOException 
	{
		client.joinGame(info, username);
	}
	
	public void startGame()
	{
		client.startGame();
		switchPage(gamePage);
	}

}
