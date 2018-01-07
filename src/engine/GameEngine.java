package engine;

import connector.ServerConnector;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import Logger.Log;
import connector.ClientConnector;

public class GameEngine {
	ServerConnector connection;
	ArrayList<Tank> tanks = new ArrayList<Tank>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public static final int FPS = 50;
	public static final int TANK_COUNT = 2;
	public static final double BULLET_WIDTH = 0.01;
	public static final double BULLET_HEIGHT = 0.01;
	public static final double BOARD_MAX_X = 1;
	public static final double BOARD_MAX_Y = 1;
	public static final double TANK_MOVEMENT_DISTANCE = 0.006;
	public static final double BULLET_MOVEMENT_DISTANCE = 0.01;
	 
	 
	public void startGame(int tankCount) {
		try {
			Log.message("Starting server");
			initializeTanks(tankCount);
			connection = new ServerConnector();
			connection.initializeServerConnection(1);
			Log.message("Clients connected");
			
			//The server will send the initial information first, such that the clients have something to display:
			
			connection.sendUpdates(tanks, bullets);
			Log.message("Sent first update");
			
			Thread.sleep(2000);
			
			//Then the main loop can begin:
			
			while(true) { //Game loop			
				Input[] userInputs = connection.reciveUserInputs();
				//Log.message(userInputs[0].toString());
				//Log.message("Received inputs from clients");
				update(userInputs);
				//Log.message("Updated game");
				connection.sendUpdates(tanks, bullets);
				//Log.message("Sent game state update");
				
				Thread.sleep(1000 / FPS);
			}	
		} catch (Exception e) {
			Log.exception(e);
		}
	}
	
	private void initializeTanks(int tankCount) {
		for(int i = 0; i < tankCount; i++) {
			
			double xNew = Math.random();
			double yNew = Math.random();
			Tank newTank = new Tank(xNew, yNew, 0, 0, i);
			
			tanks.add(newTank);
		}
		
	}


	public void update(Input[] inputs) {
		for (int i = 0; i < tanks.size(); i++) {
			final Tank tank = tanks.get(i);
			final Input currInput = inputs[i];
			
			//Update gun angle before shooting or moving
			updateGunAngle(tank, currInput.x, currInput.y);
			
			// Angle tank before moving
			if(currInput.a == true) { angleTank(tank, false); }
			if(currInput.d == true) { angleTank(tank, true); }
			
			//Move with new angle
			if(currInput.w == true) { moveTank(tank, true); }
			if(currInput.s == true) { moveTank(tank, false); }
		}
		
		//Create bullet
		for (int i = 0; i < tanks.size(); i++) {
			final Tank tank = tanks.get(i);
			final Input currInput = inputs[i];
			
			if(currInput.click == true) {
				createBullet(tank);
			}
		}
		
		//Update the locations of the bullets and decide if any hit
		final Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			final Bullet bullet = bulletIterator.next();
			
			if (!updateBulletLocation(bullet)) {
				bulletIterator.remove();
			}	
		}
	}
	
	// returns false if bullet must be deleted
	private Boolean updateBulletLocation(Bullet bullet) {
		
		// Work out changes in x and y given bullets angle and movement distance
		bullet.x += Math.cos(bullet.angle) * BULLET_MOVEMENT_DISTANCE; 
		bullet.y += Math.sin(bullet.angle) * BULLET_MOVEMENT_DISTANCE;
		
		//check if bullet has left map yet
		if ((0 < bullet.x && bullet.x < BOARD_MAX_X) && 
			(0 < bullet.y && bullet.y < BOARD_MAX_Y)) {
			return !checkDamage(bullet);
		} else {
			return false;
		}
		
	}

	//returns true if bullet hits
	private Boolean checkDamage(Bullet bullet) {
		final Point2D.Double bulletPos = new Point2D.Double(bullet.x, bullet.y);
		
		final Iterator<Tank> tankIterator = tanks.iterator();
		while (tankIterator.hasNext()) {
			final Tank tank = tankIterator.next();
			final Polygon tankPolygon =tank.getTankRectangle();
			
			if (tankPolygon.contains(bulletPos)) {
				tank.takeDamage(Bullet.BULLET_DAMAGE);
				if (tank.health == 0) {
					tankIterator.remove();
				}
				return true;
			}
		}
		return false;
	}


	private void updateGunAngle(Tank currTank, double pointerX, double pointerY) {
		final double x = pointerX - currTank.x;
		final double y = pointerY - currTank.y;
		
		currTank.gunAngle = Math.atan2(y, x);
	}

	private void createBullet(Tank currTank) {
		final Point2D.Double bulletStartPos = currTank.getBulletStartPos();
		bullets.add(new Bullet(bulletStartPos.x, bulletStartPos.y, BULLET_WIDTH, BULLET_HEIGHT, currTank.gunAngle));
	}

	// true: clockwise, false: counterclockwise
	private void angleTank(Tank currTank, boolean angle ) {
		currTank.bodyAngle += angle ? Math.toRadians(2) : -Math.toRadians(2);
	}

	// true: forward, false: backward
	private void moveTank(Tank currTank, Boolean direction) {
		final double x = Math.cos( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE * (direction ? -1 : 1);
		final double y = Math.sin( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE * (direction ? -1 : 1);
		final double possibleX = currTank.x + x; 
		final double possibleY = currTank.y + y;
		if (0 < possibleX && possibleX < BOARD_MAX_X) {
			currTank.x += x; 
		}
		if (0 < possibleY && possibleY < BOARD_MAX_Y) {
			currTank.y += y;
		}
	}

	public ArrayList<Tank> getTanks() {
		return tanks;		
	}
	
	public ArrayList<Bullet> getBullets() {
		return bullets;	
	}
	
}
