package network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import logger.Log;
import network.NetworkTools;
import network.spaces.BasicClient;
import network.spaces.ServerInfo;

public class UDPConnector {
	private static HashSet<Integer> isListening = new HashSet<Integer>();
	private static HashMap<Integer, ArrayList<UDPPacketListener>> listenersByPort = new HashMap<Integer, ArrayList<UDPPacketListener>>();
	private static DatagramSocket broadcastSocket = null;
	private static Object broadcastLock = new Object();
	
	public static synchronized void broadcastData(byte[] data, int dstPort) throws IOException
	{		
		synchronized (broadcastLock) {
			if (broadcastSocket == null) {
				broadcastSocket = new DatagramSocket();
				broadcastSocket.setBroadcast(true);
			}
			
			final ArrayList<InetAddress> broadcastAddresses = NetworkTools.getBroadcastAddresses();
			for (InetAddress address : broadcastAddresses) {
				broadcastSocket.send(new DatagramPacket(data, data.length, address, dstPort));
			}		
		}
	}
	
	public static void startListeningForBroadcasts(int listenPort)
	{
		//can be called by multiple times but should only be started for every port
		if (isListening.contains(listenPort)) {
			return;
		}
		isListening.add(listenPort);
		new Thread(() ->
		{
			try (DatagramSocket socket = new DatagramSocket(listenPort))
			{
				while (true) {
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(receivePacket);
					final ArrayList<UDPPacketListener> listeners = listenersByPort.get(listenPort);
					if (listeners != null) {
						synchronized (listenersByPort) {
							for (UDPPacketListener listener : listenersByPort.get(listenPort)) {
								listener.packetReceived(receivePacket.getData());
							}	
						}	
					}
				}
			} catch (Exception e) {
				Log.exception(e);
			}
		}).start();
	}
	
	public static void addUDPPacketListener(int listenPort, UDPPacketListener listener)
	{
		synchronized (listenersByPort) {
			if (!listenersByPort.containsKey(listenPort)) {
				listenersByPort.put(listenPort, new ArrayList<UDPPacketListener>());
			}
			final ArrayList<UDPPacketListener> listeners = listenersByPort.get(listenPort);
			listeners.add(listener);	
		}
	}
	
	public static void removeUDPPacketListener(int listenPort, UDPPacketListener listener)
	{
		synchronized (listenersByPort) {
			if (!listenersByPort.containsKey(listenPort)) {
				return;
			}
			final ArrayList<UDPPacketListener> listeners = listenersByPort.get(listenPort);
			listeners.remove(listener);
		}
	}
}
