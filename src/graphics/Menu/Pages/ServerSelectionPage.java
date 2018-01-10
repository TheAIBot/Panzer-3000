package graphics.Menu.Pages;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import graphics.Menu.MenuController;
import logger.Log;
import network.spaces.BasicClient;
import network.spaces.BasicServer;
import network.spaces.ServerFoundListener;
import network.spaces.ServerInfo;

public class ServerSelectionPage extends SuperPage implements ServerFoundListener {
	GamePage gamePage = new GamePage(controller, controller);
	BasicClient client = new BasicClient(controller);
	ArrayList<BasicServer> createdServers = new ArrayList<BasicServer>();
	ServerList serverListPage = new ServerList(this);
	Timer serverUpdateTimer;

	public ServerSelectionPage(MenuController control, PageRequestsListener listener) {
		super(control, listener);
		client.setServerFoaundLister(this);
	}

	@Override
	public JPanel createPage(MenuController control) {
		setResizeable(true);
		page = serverListPage;
		return page;
	}

	@Override
	public void startPage() {		
		client.startListeningForServers();
		
		serverUpdateTimer = new Timer();
		serverUpdateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					client.searchForServers();
					
					serverListPage.updateServerInfo();
				} catch (Exception e) {
					Log.exception(e);
				}
			}
		}, 0, 1000);
	}

	@Override
	public void closePage() {
		serverUpdateTimer.cancel();
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
		client.joinGame(info, username, this);
	}
	
	public void startGame()
	{
		client.startGame();
	}
	
	public void gameStarted() {
		switchPage(gamePage);
	}
	
	public void createServer(String serverName) throws UnknownHostException, SocketException {
		BasicServer server = new BasicServer(serverName);
		server.startServer();
		createdServers.add(server);
	}

}
