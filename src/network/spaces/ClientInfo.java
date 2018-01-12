package network.spaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.DeSerializer;

public class ClientInfo extends DeSerializer {
	public String username;
	public String salt;

	@Override
	public void toBytes(DataOutputStream out) throws IOException {
		out.writeUTF(username);
		out.writeUTF(salt);
	}

	@Override
	public void fromBytes(DataInputStream in) throws IOException {
		username = in.readUTF();
		salt = in.readUTF();
	}
}
