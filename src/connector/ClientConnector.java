package connector;

import java.util.*;

import org.jspace.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import engine.*;

public class ClientConnector implements Runnable{

	public RemoteSpace 	privateServerConnections;
	public RemoteSpace 	updateSpace;
	public int 			connectionId;
	
	public static void main(String[] args) {
		new ClientConnector().connectToServer();
	}
	
	public void connectToServer() {
		try {
			updateSpace		= new RemoteSpace("tcp://127.0.0.1:9001/updateSpace?keep");
			Object[] tuple 	= updateSpace.get(new FormalField(Integer.class));
			connectionId   	= (int) tuple[0];
			privateServerConnections = new RemoteSpace("tcp://127.0.0.1:9001/clientSpace" + connectionId + "?keep");
			privateServerConnections.put("connected", 0.0);
			//System.out.println("Client connected");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public Object[] recieveUpdates() {
		try {
			//TODO ask if reading puts a lock on the space.
			Object[] tuple = updateSpace.query(new FormalField(Tank[].class), new FormalField(List.class));
			return tuple;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
	public List<Tank> unpackTanks(Object[] updateTuple) {

		List<Tank> unpackedTanks = new ArrayList<Tank>();
		List<Object>  jsonTanks = (List<Object>) updateTuple[1];
		for (int i = 0; i < jsonTanks.size(); i++) {
			JsonElement tankJSonElement = new Gson().toJsonTree(jsonTanks.get(i));
			JsonObject tankJSonObject = tankJSonElement.getAsJsonObject();
			unpackedTanks.add(new Gson().fromJson(tankJSonObject, Tank.class));
		}
		return unpackedTanks;
	}
	
	public List<Bullet> unpackBullets(Object[] updateTuple) {
		List<Bullet> unpackedBullets = new ArrayList<Bullet>();
		List<Object>  jsonBullets = (List<Object>) updateTuple[1];
		for (int i = 0; i < jsonBullets.size(); i++) {
			JsonElement bulletJSonElement = new Gson().toJsonTree(jsonBullets.get(i));
			JsonObject bulletJSonObject = bulletJSonElement.getAsJsonObject();
			unpackedBullets.add(new Gson().fromJson(bulletJSonObject, Bullet.class));
		}
		return unpackedBullets;
	}
	
	
	public void sendUserInput(Input inputs) {
		privateServerConnections.put(inputs);		
	}

	@Override
	public void run() {
		connectToServer();
	}
	
	

	//sendUserInputs(Input inputs);
	//recieveUpdates();
}
