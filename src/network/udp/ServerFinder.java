package network.udp;

import engine.DeSerializer;
import logger.Log;
import network.spaces.ServerInfo;

public class ServerFinder implements UDPPacketListener {	
	public static final String BROADCAST_MESSAGE = "anyone there?";
	public static final int UDP_PORT_ASK = 3242;
	public static final int UDP_PORT_ANSWER = 3243;
	
	private ServerFoundListener listener;
	
	public void searchForServers() throws Exception {
		final byte[] data = DeSerializer.encodeObjects(BROADCAST_MESSAGE);
		UDPConnector.broadcastData(data, UDP_PORT_ASK);
	}
	
	public void startListeningForServers()
	{
		UDPConnector.startListeningForBroadcasts(UDP_PORT_ANSWER);
		UDPConnector.addUDPPacketListener(UDP_PORT_ANSWER, this);
	}
	
	public void stopListeningForServers()
	{
		UDPConnector.removeUDPPacketListener(UDP_PORT_ANSWER, this);
	}
	
	public void setServerFoundLister(ServerFoundListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void packetReceived(byte[] packetData) {
		try {
			final ServerInfo info = ServerInfo.toServerInfo(packetData);
			listener.foundServer(info);
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
