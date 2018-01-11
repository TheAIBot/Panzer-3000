package network.udp;

public interface UDPPacketListener {
	public void packetReceived(byte[] packetData);
}
