package connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.jspace.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Logger.Log;
import engine.*;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	
	public void connectToServer() throws UnknownHostException, IOException, InterruptedException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		ip = "192.168.0.17";
		updateSpace		= new RemoteSpace("tcp://" + ip + ":9001/updateSpace?keep");
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new RemoteSpace("tcp://" + ip + ":9001/clientSpace" + connectionId + "?keep");
		privateServerConnections.put("connected", connectionId);
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return updateSpace.get(new ActualField(connectionId), new FormalField(ArrayList.class), new FormalField(ArrayList.class), new FormalField(ArrayList.class));
	}	
	
	
	public ArrayList<Tank> unpackTanks(Object[] updateTuple) {
		return unpackType(updateTuple[1], Tank.class);
	}
	
	public ArrayList<Bullet> unpackBullets(Object[] updateTuple) {
		return unpackType(updateTuple[2], Bullet.class);
	}
	
	public ArrayList<Wall> unpackWalls(Object[] updateTupe) {
		return unpackType(updateTupe[3], Wall.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> unpackType(Object toUnpack, Class<T> type)
	{
		ArrayList<T> unpacked = new ArrayList<T>();
		ArrayList<Object>  jsonObjects = (ArrayList<Object>) toUnpack;
		for (int i = 0; i < jsonObjects.size(); i++) {
			JsonElement bulletJSonElement = new Gson().toJsonTree(jsonObjects.get(i));
			JsonObject bulletJSonObject = bulletJSonElement.getAsJsonObject();
			unpacked.add(new Gson().fromJson(bulletJSonObject, type));
		}
		return unpacked;
	}
	
	
	public void sendUserInput(Input input) {
		input.id = connectionId;
		privateServerConnections.put(input);		
	}

	@Override
	public void run() {
		try {
			connectToServer();			
		} catch (Exception e) { 
			Log.exception(e);
		}
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
