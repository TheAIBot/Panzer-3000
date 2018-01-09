package connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerInfo {
	public String name = "";
	public String ipAddress = "";
	public int clientsConnected = 0;
	
	public byte[] toByteArray() throws IOException
	{
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				out.writeUTF(name);
				out.writeUTF(ipAddress);
				out.writeInt(clientsConnected);
				
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
				
				return info;
			}
		}
	}
	
	@Override
	public String toString() {
		return name + ": " + clientsConnected;
	}
	
	public boolean equals(ServerInfo info) {
		return ipAddress.equals(info.ipAddress);
	}
}
