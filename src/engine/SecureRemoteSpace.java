package engine;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;

public class SecureRemoteSpace extends RemoteSpace {

	public SecureRemoteSpace(String uri) throws UnknownHostException, IOException {
		super(uri);
		// TODO Auto-generated constructor stub
	}
	
	public void Put(Object fields)  {
		super.put(fields);
		// TODO Auto-generated constructor stub
	}

}
