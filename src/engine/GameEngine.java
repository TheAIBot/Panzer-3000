package engine;

import connector.ServerConnector;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

import connector.ClientConnector;

public class GameEngine {
	ServerConnector serverConnector;
	ClientConnector clientConnector;
	ArrayList<Tank> tanks;
	ArrayList<Bullet> bullets;
	public static final int TANK_COUNT = 2;
	public static final double TANK_WIDTH = 0.005;
	public static final double TANK_HEIGHT = 0.005;
	public static final double BULLET_WIDTH = 0.001;
	public static final double BULLET_HEIGHT = 0.001;
	public static final double BOARD_MAX_X = 1;
	public static final double BOARD_MAX_Y = 1;
	public static final double TANK_MOVEMENT_DISTANCE = 0.001;
	public static final double BULLET_MOVEMENT_DISTANCE = 0.01;
	

	public static void main(String[] args) {
		new GameEngine().startGame(TANK_COUNT);
	}
	 
	 
	public void startGame(int tankCount) {
		
		serverConnector = new ServerConnector();
		clientConnector = new ClientConnector();
		
		initializeTanks(tankCount);
		
		while(true) {
			
			update(serverConnector.reciveUserInputs());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	private void initializeTanks(int tankCount) {
		for(int i = 0; i < tankCount; i++) {
			
			double xNew = Math.random() % BOARD_MAX_X;
			double yNew = Math.random() % BOARD_MAX_Y;
			Tank newTank = new Tank(xNew, yNew, TANK_WIDTH, TANK_HEIGHT, 0, 0, i);
			
			tanks.add(newTank);
		}
		
	}


	public void update(Input[] inputs) {
		
		ArrayList<Tank> currTanks = getTanks();
		ArrayList<Bullet> currBullets = getBullets();

		
		for(int x = 0; x < inputs.length; x++) {
			Input currInput = inputs[x];
			
			//Deal with tank related inputs
			for(int y = 0; x < currTanks.size(); x++) {
				if(currTanks.get(y).id == currInput.id) {

					//Update gun angle before shooting or moving
					updateGunAngle(currTanks.get(y), currInput.x, currInput.y);
					
					//Create bullet
					if(currInput.click == true) {
						createBullet(currTanks.get(y));
					}
					
					// Angle tank before moving
					if(currInput.a == true) { angleTank(currTanks.get(y), false); }
					if(currInput.d == true) { angleTank(currTanks.get(y), true); }
					
					//Move with new angle
					if(currInput.w == true) { moveTank(currTanks.get(y), true); }
					if(currInput.s == true) { moveTank(currTanks.get(y), false); }
					
					
				}
			}
		}
		
		//Update the locations of the bullets and decide if any hit
		for (int x = 0; x < bullets.size(); x++) {
			
			//if the bullet has left the map, delete it
			if (!updateBulletLocation(currBullets.get(x))) {
				bullets.remove(x);
			}
		}
		
	}
	
	// returns false if bullet must be deleted
	private Boolean updateBulletLocation(Bullet bullet) {
		
		// Work out changes in x and y given bullets angle and movement distance
		bullet.x += Math.sin( bullet.angle ) * BULLET_MOVEMENT_DISTANCE; 
		bullet.y += Math.cos( bullet.angle ) * BULLET_MOVEMENT_DISTANCE;
		
		//check if bullet has left map yet
		if ((0 < bullet.x && bullet.x < BOARD_MAX_X) && (0 < bullet.y && bullet.y < BOARD_MAX_Y)) {
			return !checkDamage(bullet);
		} else {
			return false;
		}
		
	}

	//returns true if bullet hits
	private Boolean checkDamage(Bullet bullet) {
		
		Polygon bulletPolygon = bullet.getBulletRectangle();
		
		for(int i = 0; i < tanks.size(); i++) { 
			Polygon tankPolygon = tanks.get(i).getTankRectangle();
		
			if (intersects(bulletPolygon, tankPolygon)) {
				tanks.get(i).takeDamage(Bullet.BULLET_DAMAGE);
				if (tanks.get(i).health == 0 ) {
					tanks.remove(i);
				}
				return true;
			}
		}
		return false;
	}

	private boolean intersects(Polygon bulletPolygon, Polygon tankPolygon) {
		Area bulletArea = new Area(bulletPolygon);
		Area tankArea = new Area(tankPolygon);
		
		bulletArea.intersect(tankArea);
		return 	!bulletArea.isEmpty();
	}


	private void updateGunAngle(Tank currTank, double pointerX, double pointerY) {
		// TODO Auto-generated method stub
		
		double x = pointerX - currTank.x;
		double y = pointerY - currTank.y;
		
		double radianAngle = Math.atan(Math.abs(x/y));
		
		if (x > 0 && y < 0) {
			radianAngle = Math.toRadians(180) - radianAngle;
		} else if (x < 0 && y < 0) {
			radianAngle += Math.toRadians(180);
		} else if (x < 0 && y > 0) {
			radianAngle = Math.toRadians(360) - radianAngle;
		}
		
		currTank.gunAngle = radianAngle;
	}

	private void createBullet(Tank currTank) {

		Bullet newBullet = new Bullet(currTank.x, currTank.y, BULLET_WIDTH, 
				BULLET_HEIGHT, currTank.gunAngle);
		bullets.add(newBullet);

	}

	// true: clockwise, false: counterclockwise
	private void angleTank(Tank currTank, Boolean angle ) {
		if (angle) { currTank.bodyAngle += Math.toRadians(10); }
		else { currTank.bodyAngle -= Math.toRadians(10); }
	}

	// true: forward, false: backward
	private void moveTank(Tank currTank, Boolean direction) {
		double x = Math.sin( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE;
		double y = Math.cos( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE;
		if(direction) {
			double possibleX = currTank.x + x; 
			double possibleY = currTank.y + y;
			if (possibleX < BOARD_MAX_X) {
				currTank.x += x; 
			}
			if (possibleY < BOARD_MAX_Y) {
				currTank.y += y;
			}
		} else {
			double possibleX = currTank.x - x; 
			double possibleY = currTank.y - y;

			if (possibleX > 0) {
				currTank.x -= x; 
			}
			if (possibleY > 0) {
				currTank.y -= y;
			}
		}
	}

	public ArrayList<Tank> getTanks() {
		return tanks;		
	}
	
	public ArrayList<Bullet> getBullets() {
		return bullets;	
	}
	
}
