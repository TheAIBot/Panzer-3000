package connector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Logger.Log;
import engine.GameEngine;

public class BasicServer {
	ServerInfo info;
	
	public static void main(String[] args) {
		BasicServer server = new BasicServer();
		try {
			server.startServer();
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public void startServer() throws UnknownHostException {
		info = new ServerInfo();
		info.ipaddress = InetAddress.getLocalHost().getHostAddress();
		
		new Thread(() -> receiveBroadcasts()).start();
	}
	
	private void receiveBroadcasts()
	{
		try (DatagramSocket socket = new DatagramSocket(BasicClient.UDP_PORT))
		{
			socket.setReuseAddress(true);
			final ArrayList<InetAddress> broadcastAddresses = BasicClient.getBroadcastAddresses();
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				Log.message("Server received a udp message");
				try {
					String message = BasicClient.bytesToString(receivePacket.getData());
					Log.message("Server received message: " + message);
					if (message.equals(BasicClient.BROADCAST_MESSAGE)) {
						byte[] sendData = info.toByteArray();
						for (InetAddress address : broadcastAddresses) {
							BasicClient.broadcastUDPMessage(socket, sendData, address);
						}
						Log.message("Server sent server information to all clients");
					}
				} catch (Exception e) {	
					Log.exception(e);
				}
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	public void startGame() {
		new GameEngine().startGame(2);
	}
}
