package network.spaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerInfo {
	public String name = "";
	public String ipAddress = "";
	public int clientsConnected = 0;
	public int port;
	
	public byte[] toByteArray() throws IOException
	{
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				out.writeUTF(name);
				out.writeUTF(ipAddress);
				out.writeInt(clientsConnected);
				out.writeInt(port);
				
				return stream.toByteArray();
			}
		}
	}
	
	public static ServerInfo toServerInfo(byte[] bytes) throws IOException
	{
		try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
			try (DataInputStream in = new DataInputStream(stream)) {
				ServerInfo info = new ServerInfo();
				info.name = in.readUTF();
				info.ipAddress = in.readUTF();
				info.clientsConnected = in.readInt();
				info.port = in.readInt();
				
				return info;
			}
		}
	}
	
	@Override
	public String toString() {
		return name + ": " + clientsConnected;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ServerInfo)) {
			return false;
		}
		
		ServerInfo info = (ServerInfo) obj;
		return this.equals(info);
	}
	
	public boolean equals(ServerInfo info) {
		return ipAddress.equals(info.ipAddress) && port == info.port;
	}
}
