package Menu.Pages;

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
import network.CommunicationType;
import network.spaces.ServerInfo;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JSeparator;

public class ServerList extends JPanel implements ListSelectionListener {
	private final ServerSelectionPage serverPage;
	private ServerInfo selectedInfo;
	
	private JTextField textFieldUsername;
	private JTextField textFieldServerName;
	
	private final DefaultListModel<ServerInfo> listData = new DefaultListModel<ServerInfo>();
	private final JList<ServerInfo> list = new JList<ServerInfo>(listData);
	
	private final JLabel lblServerName = new JLabel("");
	private final JLabel lblPlayerCount = new JLabel("");
	
	private final JButton btnStartGame = new JButton("Start game");
	private final JButton btnCreateServer = new JButton("Create server");
	private final JButton btnCreateP2PServer = new JButton("Create p2p server");

	/**
	 * Create the panel.
	 */
	public ServerList(ServerSelectionPage serverPage) {
		this.serverPage = serverPage;
		
		setLayout(null);
		
		list.setBounds(10, 11, 199, 209);
		add(list);
		list.addListSelectionListener(this);
		
		JLabel lbl1 = new JLabel("Server:");
		lbl1.setBounds(219, 11, 99, 15);
		add(lbl1);
		
		JLabel lbl2 = new JLabel("Player count:");
		lbl2.setBounds(219, 38, 99, 15);
		add(lbl2);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(10, 232, 87, 15);
		add(lblUsername);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(95, 230, 114, 19);
		add(textFieldUsername);
		textFieldUsername.setColumns(10);

		lblServerName.setBounds(330, 11, 123, 15);
		add(lblServerName);
		
		lblPlayerCount.setBounds(330, 38, 123, 15);
		add(lblPlayerCount);
		
		
		btnStartGame.setBounds(219, 64, 264, 63);
		add(btnStartGame);
		btnStartGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedInfo != null) {
					try {
						serverPage.requestStartGame();
					} catch (InterruptedException e1) {
						Log.exception(e1);
					}
				}
			}
		});
		
		btnCreateServer.setBounds(354, 184, 109 + 20, 63);
		add(btnCreateServer);
		btnCreateServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String serverName = textFieldServerName.getText();
				//don't create a server with an empty name
				if (!serverName.trim().isEmpty()) {
					try {
						serverPage.createServer(serverName, CommunicationType.SERVER_CLIENT);
					} catch (Exception e1) {
						Log.exception(e1);
					}
				}
			}
		});
		
		btnCreateP2PServer.setBounds(219, 184, 109 + 20, 63);
		add(btnCreateP2PServer);
		btnCreateP2PServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String serverName = textFieldServerName.getText();
				//don't create a server with an empty name
				if (!serverName.trim().isEmpty()) {
					try {
						serverPage.createServer(serverName, CommunicationType.P2P);
					} catch (Exception e1) {
						Log.exception(e1);
					}
				}
			}
		});
		
		JLabel lblServerName_1 = new JLabel("Server name: ");
		lblServerName_1.setBounds(219, 158, 68, 15);
		add(lblServerName_1);
		
		textFieldServerName = new JTextField();
		textFieldServerName.setBounds(297, 155, 187, 19);
		add(textFieldServerName);
		textFieldServerName.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(219, 138, 264, 9);
		add(separator);

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

	@Override
	public synchronized void valueChanged(ListSelectionEvent arg0) {
		if (!textFieldUsername.getText().isEmpty()) {
			try {
				final ServerInfo info = listData.get(list.getSelectedIndex());
				if (selectedInfo == null || !selectedInfo.equals(info)) {
					serverPage.joinGame(info, textFieldUsername.getText());
					updateServerInfo(info);
				}
			} catch (Exception e) {
				Log.exception(e);
			}		
		}	
		else {
			list.clearSelection();
		}
	}
	
	public void updateServerInfo() throws Exception
	{
		if (selectedInfo != null) {
			updateServerInfo(selectedInfo);	
			list.repaint();
		}
	}
	
	private synchronized void updateServerInfo(ServerInfo info) throws Exception
	{
		final int playerCount = serverPage.getPlayerCount(info);
		
		//only set selectedInfo if getPlayerCount doesn't crash because
		//that means the server is still running for the moment
		selectedInfo = info;
		selectedInfo.clientsConnected = playerCount;
		setServerName(selectedInfo.name);
		setPlayerCount(playerCount);
	}
}
