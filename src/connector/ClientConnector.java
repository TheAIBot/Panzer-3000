package connector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import org.jspace.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.corba.se.spi.activation.Server;
import com.sun.swing.internal.plaf.metal.resources.metal;

import Logger.Log;
import engine.*;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	public String		username;
	public int 			numberOfClients;
	
	public void connectToServer(String username) throws UnknownHostException, IOException, InterruptedException {
		this.username = username;
		updateSpace		= new RemoteSpace("tcp://" + ServerConnector.IP_ADDRESS + ":9001/updateSpace?keep");
		List<Object[]> tuples = updateSpace.queryAll(new FormalField(Object.class), new FormalField(Object.class));
		Object[] tuple1 = updateSpace.query(new ActualField("numClients"), new FormalField(Integer.class));
		numberOfClients = (int) tuple1[1];
		Object[] tuple 	= updateSpace.get(new FormalField(Integer.class), new ActualField(username));
		connectionId   	= (int) tuple[0];
		privateServerConnections = new RemoteSpace("tcp://" + ServerConnector.IP_ADDRESS + ":9001/clientSpace" + connectionId + "?keep");
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
			connectToServer(username);			
		} catch (Exception e) { 
			Log.exception(e);
		}
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
