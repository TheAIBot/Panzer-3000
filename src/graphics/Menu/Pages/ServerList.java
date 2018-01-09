package graphics.Menu.Pages;

import javax.swing.JPanel;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import connector.ServerInfo;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class ServerList extends JPanel {
	private final ServerSelectionPage serverPage;
	private JTextField textFieldUsername;
	private JTextField textField;
	
	private final DefaultListModel<ServerInfo> listData = new DefaultListModel<ServerInfo>();
	private final JList<ServerInfo> list = new JList<ServerInfo>(listData);
	
	private final DefaultListModel<String> listPlayersData = new DefaultListModel<String>();
	private final JList<String> listPlayers = new JList<String>(listPlayersData);

	/**
	 * Create the panel.
	 */
	public ServerList(ServerSelectionPage serverPage) {
		this.serverPage = serverPage;
		
		setLayout(null);
		
		list.setBounds(12, 12, 199, 485);
		add(list);
		
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
		
		JLabel lblServerName = new JLabel("");
		lblServerName.setBounds(334, 13, 123, 15);
		add(lblServerName);
		
		JLabel lblPlayerCount = new JLabel("");
		lblPlayerCount.setBounds(334, 40, 123, 15);
		add(lblPlayerCount);
		
		listPlayers.setBounds(223, 67, 160, 188);
		add(listPlayers);
		
		JButton btnStartGame = new JButton("Start game");
		btnStartGame.setBounds(457, 192, 222, 63);
		add(btnStartGame);
		
		JButton btnCreateServer = new JButton("Create server");
		btnCreateServer.setBounds(457, 461, 222, 63);
		add(btnCreateServer);
		
		JLabel lblServerName_1 = new JLabel("Server name: ");
		lblServerName_1.setBounds(457, 432, 99, 15);
		add(lblServerName_1);
		
		textField = new JTextField();
		textField.setBounds(556, 430, 123, 19);
		add(textField);
		textField.setColumns(10);

	}

	public void addServer(ServerInfo info)
	{
		listData.addElement(info);
	}
	
	public void updatePlayersList(String[] playerNames)
	{
		listPlayersData.clear();
		for (String playerName : playerNames) {
			listPlayersData.addElement(playerName);
		}
	}
}
