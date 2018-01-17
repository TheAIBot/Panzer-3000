package network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkTools {
	private static String ownIP = null;
	private static ArrayList<InetAddress> broadcastAddresses = null;
	
	public static String getIpAddress() throws UnknownHostException, SocketException {
		if (ownIP != null) {
			return ownIP;
		}
		
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = networkInterfaces.nextElement();
			if (!netInterface.isVirtual() && 
				!netInterface.isPointToPoint() && 
				!netInterface.isLoopback() && 
				netInterface.isUp()) {
				for (InterfaceAddress ia : netInterface.getInterfaceAddresses()) {
					if (ia.getAddress() != null && ia.getAddress() instanceof Inet4Address) {
						ownIP = ia.getAddress().getHostAddress();
						return ownIP;
					}
				}
			}
		}
		throw new UnknownHostException("Failed to find this computers ipaddress");
	}

	public static ArrayList<InetAddress> getBroadcastAddresses() throws SocketException {
		if (broadcastAddresses != null) {
			return broadcastAddresses;
		}
		
		final ArrayList<InetAddress> validBroadcastAddresses = new ArrayList<InetAddress>();
		final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = networkInterfaces.nextElement();
			if (!netInterface.isVirtual() && 
				!netInterface.isPointToPoint() && 
				!netInterface.isLoopback() && 
				netInterface.isUp()) {
				for (InterfaceAddress ia : netInterface.getInterfaceAddresses()) {
					final InetAddress broadcastAddress = ia.getBroadcast();
					if (broadcastAddress != null) {
						validBroadcastAddresses.add(broadcastAddress);
					}
				}
			}
		}
		broadcastAddresses = validBroadcastAddresses;
		return broadcastAddresses;
	}
	
	public static URI createURI(NetworkProtocol protocol, String ipAddress, int port, String spaceName, String mode) throws URISyntaxException {
		return new URI(protocol.toString() + "://" + ipAddress + ":" + port + "/" + spaceName + "?" + mode);
	}

	public static int getRandomPort() {
		return (int)(Math.random() * Short.MAX_VALUE) + 1025;
	}
}
