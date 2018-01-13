package Menu.Pages;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import Menu.MenuController;
import logger.Log;
import network.spaces.BasicClient;
import network.spaces.BasicServer;
import network.spaces.ServerFoundListener;
import network.spaces.ServerInfo;
import network.udp.ServerFinder;

public class ServerSelectionPage extends SuperPage implements ServerFoundListener {
	BasicClient client = new BasicClient(controller);
	ServerFinder serverFinder = new ServerFinder();
	ArrayList<BasicServer> createdServers = new ArrayList<BasicServer>();
	ServerList serverListPage = new ServerList(this);
	Timer serverUpdateTimer;

	public ServerSelectionPage(MenuController control, PageRequestsListener listener) {
		super(control, listener);
		serverFinder.setServerFoundLister(this);
	}

	@Override
	public JPanel createPage(MenuController control) {
		setResizeable(true);
		page = serverListPage;
		return page;
	}

	@Override
	public void startPage() {		
		serverFinder.startListeningForServers();
		try {
			serverFinder.searchForServers();
		} catch (Exception e1) {
			Log.exception(e1);
		}
		
		serverUpdateTimer = new Timer();
		serverUpdateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					serverFinder.searchForServers();
					
					serverListPage.updateServerInfo();
				} catch (Exception e) {	}
			}
		}, 0, 1000);
	}

	@Override
	public void closePage() {
		serverUpdateTimer.cancel();
		serverFinder.stopListeningForServers();
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
	
	public int getPlayerCount(ServerInfo info) throws Exception {
		return client.getPlayerCount(info);
	}
	
	public void joinGame(ServerInfo info, String username) throws Exception 
	{
		if (client.hasJoinedAGame()) {
			client.leaveGame();
			serverListPage.updateServerInfo();
		}
		client.joinGame(info, username, controller);
	}
	
	public void requestStartGame()
	{
		serverUpdateTimer.cancel();
		serverFinder.stopListeningForServers();
		client.requestStartGame();
	}
	
	public void createServer(String serverName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		BasicServer server = new BasicServer(serverName);
		server.startServer();
		createdServers.add(server);
	}

}
