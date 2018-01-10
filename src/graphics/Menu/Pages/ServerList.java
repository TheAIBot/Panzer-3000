package graphics.Menu.Pages;

import javax.swing.JPanel;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import logger.Log;
import network.spaces.ServerInfo;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class ServerList extends JPanel implements ListSelectionListener {
	private final ServerSelectionPage serverPage;
	private ServerInfo selectedInfo;
	
	private JTextField textFieldUsername;
	private JTextField textFieldServerName;
	
	private final DefaultListModel<ServerInfo> listData = new DefaultListModel<ServerInfo>();
	private final JList<ServerInfo> list = new JList<ServerInfo>(listData);
	
	private final DefaultListModel<String> listPlayersData = new DefaultListModel<String>();
	private final JList<String> listPlayers = new JList<String>(listPlayersData);
	
	private final JLabel lblServerName = new JLabel("");
	private final JLabel lblPlayerCount = new JLabel("");
	
	private final JButton btnStartGame = new JButton("Start game");
	private final JButton btnCreateServer = new JButton("Create server");

	/**
	 * Create the panel.
	 */
	public ServerList(ServerSelectionPage serverPage) {
		this.serverPage = serverPage;
		
		setLayout(null);
		
		list.setBounds(12, 12, 199, 485);
		add(list);
		list.addListSelectionListener(this);
		
		JLabel lbl1 = new JLabel("Server:");
		lbl1.setBounds(223, 13, 99, 15);
		add(lbl1);
		
		JLabel lbl2 = new JLabel("Player count:");
		lbl2.setBounds(223, 40, 99, 15);
		add(lbl2);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(12, 509, 87, 15);
		add(lblUsername);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(97, 507, 114, 19);
		add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(223, 267, 456, 15);
		add(horizontalStrut);

		lblServerName.setBounds(334, 13, 123, 15);
		add(lblServerName);
		
		lblPlayerCount.setBounds(334, 40, 123, 15);
		add(lblPlayerCount);
		
		listPlayers.setBounds(223, 67, 160, 188);
		add(listPlayers);
		
		
		btnStartGame.setBounds(457, 192, 222, 63);
		add(btnStartGame);
		btnStartGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedInfo != null) {
					serverPage.requestStartGame();
				}
			}
		});
		
		btnCreateServer.setBounds(457, 461, 222, 63);
		add(btnCreateServer);
		btnCreateServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String serverName = textFieldServerName.getText();
				//don't create a server with an empty name
				if (!serverName.trim().isEmpty()) {
					try {
						serverPage.createServer(serverName);
					} catch (Exception e1) {
						Log.exception(e1);
					}
				}
			}
		});
		
		JLabel lblServerName_1 = new JLabel("Server name: ");
		lblServerName_1.setBounds(457, 432, 99, 15);
		add(lblServerName_1);
		
		textFieldServerName = new JTextField();
		textFieldServerName.setBounds(556, 430, 123, 19);
		add(textFieldServerName);
		textFieldServerName.setColumns(10);

	}

	public synchronized void addServer(ServerInfo info)
	{
		if (!listData.contains(info)) {
			listData.addElement(info);
		}

	}
	
	public synchronized void clearServerList()
	{
		listData.clear();
	}
	
	private void setServerName(String name) 
	{
		lblServerName.setText(name);
	}
	
	private void setPlayerCount(int playerCount) 
	{
		lblPlayerCount.setText("" + playerCount);
	}
	
	private void setPlayersList(String[] playerNames)
	{
		listPlayersData.clear();
		for (String playerName : playerNames) {
			listPlayersData.addElement(playerName);
		}
	}

	@Override
	public synchronized void valueChanged(ListSelectionEvent arg0) {
		try {
			final ServerInfo info = listData.get(arg0.getFirstIndex());
			if (selectedInfo == null || !selectedInfo.equals(info)) {
				serverPage.joinGame(info, textFieldUsername.getText());
				updateServerInfo(info);
			}
		} catch (Exception e) {
			Log.exception(e);
		}		
	}
	
	public void updateServerInfo() throws UnknownHostException, InterruptedException, IOException
	{
		if (selectedInfo != null) {
			updateServerInfo(selectedInfo);	
		}
	}
	
	private synchronized void updateServerInfo(ServerInfo info) throws UnknownHostException, InterruptedException, IOException
	{
		String[] playerNames = serverPage.getPlayerNames(info);
		setPlayersList(playerNames);
		
		//only set selectedInfo if getPlayerNames doesn't crash because
		//that means the server is still running for the moment
		selectedInfo = info;
		selectedInfo.clientsConnected = playerNames.length;
		setServerName(selectedInfo.name);
		setPlayerCount(playerNames.length);
		//data about the server may have changed in the server list
		//so update by adding an removing a dummy element.
		listData.addElement(new ServerInfo());
		listData.removeElementAt(listData.size() - 1);
	}
}
