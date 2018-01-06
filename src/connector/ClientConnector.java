package connector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import org.jspace.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import Logger.Log;
import engine.*;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	
	public void connectToServer() throws UnknownHostException, IOException, InterruptedException {
		updateSpace		= new RemoteSpace("tcp://localhost:9001/updateSpace?keep");
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new RemoteSpace("tcp://localhost:9001/clientSpace" + connectionId + "?keep");
		privateServerConnections.put("connected", 0.0);
	}
	
	public Object[] recieveUpdates() throws InterruptedException {
		return updateSpace.get(new ActualField(connectionId), new FormalField(ArrayList.class), new FormalField(ArrayList.class));
	}	
	
	
	public ArrayList<Tank> unpackTanks(Object[] updateTuple) {
		return unpackType(updateTuple[1], Tank.class);
	}
	
	public ArrayList<Bullet> unpackBullets(Object[] updateTuple) {
		return unpackType(updateTuple[2], Bullet.class);
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
	
	
	public void sendUserInput(Input inputs) {
		privateServerConnections.put(inputs);		
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
