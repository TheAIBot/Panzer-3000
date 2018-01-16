package network.spaces;

import java.io.IOException;
import java.util.ArrayList;

import org.jspace.SequentialSpace;

import engine.GameEngine;
import engine.Input;
import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;

public class PeerDummyServerConnector extends SuperServerConnector {
	private ArrayList<Wall> walls;
	private ArrayList<Tank> tanks;
	private ArrayList<Bullet> bullets;
	private ArrayList<Powerup> powerups;
	private Input[] inputs;

	@Override
	public void initializeServerConnection(int port, ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws Exception { }
	
	@Override
	public void initilizePrivateConnections(ClientInfo[] clientInfos, SequentialSpace startServerSpace) throws InterruptedException { }

	@Override
	public void sendWalls(ArrayList<Wall> walls) throws Exception {
		this.walls = walls;
	}

	@Override
	public void sendUpdate(ArrayList<Tank> tanks, ArrayList<Bullet> bullets, ArrayList<Powerup> powerups) throws Exception {
		this.tanks = tanks;
		this.bullets = bullets;
		this.powerups = powerups;
	}
	
	@Override
	public Input[] receiveUserInputs() throws InterruptedException, IOException {
		return inputs;
	}

	@Override
	public void closeConnections() { }

	public ArrayList<Wall> getWalls() {
		return walls;
	}
	
	public Object[] getUpdate() {
		return new Object[] {tanks, bullets, powerups};
	}
	
	public void setInputs(Input[] inputs) {
		this.inputs = inputs;
	}
}
