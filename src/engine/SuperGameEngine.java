package engine;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;

import logger.Log;
import network.spaces.BasicServer;
import network.spaces.DirectServerConnector;
import network.spaces.SuperServerConnector;

public abstract class SuperGameEngine {
	protected SuperServerConnector connection;
	protected Random random 				= new Random();
	protected ArrayList<Tank> tanks 		= new ArrayList<Tank>();
	protected ArrayList<Bullet> bullets 	= new ArrayList<Bullet>();
	protected ArrayList<Wall> walls 		= new ArrayList<Wall>();
	protected ArrayList<Powerup> powerups 	= new ArrayList<Powerup>();
	public boolean gameHasBeenWon 			= false;
	
	public static final int FPS 				= 60;
	public static final double BOARD_MAX_X 		= 1;
	public static final double BOARD_MAX_Y 		= 1;
	public static final boolean LOAD_LEVEL 		= true;
	public static final String LEVEL_NAME 		= "basic";
	public static final String LEVEL_DIRECTORY 	= "src/levels/";
	public static final double TANK_MOVEMENT_DISTANCE = 0.006;
	 
	 
	public abstract void startGame(int port, int tankCount, String[] ipaddresses, String[] usernames, SuperServerConnector connection, SequentialSpace startServerSpace);

	public static boolean hasTankWonGame(ArrayList<Tank> tanks, int numberOfClients) {
		return tanks.size() <= 1 && tanks.size() != numberOfClients;
	}
	
	
	public void initializeGame(int tankCount, String[] usernames) {
		initializeWalls();
		initializeTanks(tankCount);

		for (int i = 0; i < tanks.size(); i++) {
			tanks.get(i).userName = usernames[tanks.get(i).id];
		}	
	}

	protected void initializeTanks(int tankCount) {
		for (int i = 0; i < tankCount; i++) {
			Tank newTank;

			do {
				final double xNew = random.nextDouble();
				final double yNew = random.nextDouble();
				newTank = new Tank(xNew, yNew, 0, 0, i);
				// tank shouldn't spawn inside a wall
			} while (isTankInsideAnyWall(newTank));

			tanks.add(newTank);
		}
	}

	protected void initializeWalls() {
		// top wall
		walls.add(new Wall(0, -1, 1, 1));
		// left wall
		walls.add(new Wall(-1, 0, 1, 1));
		// bottom wall
		walls.add(new Wall(0, 1, 1, 1));
		// right wall
		walls.add(new Wall(1, 0, 1, 1));

		
		if (LOAD_LEVEL) {
			loadLevel(LEVEL_NAME);
		} else {
			
			for (int i = 0; i < 10; i++) {
				walls.add(new Wall(random.nextDouble(), random.nextDouble(), 0.1, 0.1));
			}
			
			byte[] levelBytes;
			try {
				levelBytes = DeSerializer.toBytes(walls);
				Path path = Paths.get(LEVEL_DIRECTORY + "random.lvl");
				Files.write(path, levelBytes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void loadLevel(String levelName) {
		byte[] levelBytes;
		try {
			Path path = Paths.get(LEVEL_DIRECTORY + levelName + ".lvl");
			levelBytes = Files.readAllBytes(path);
			walls = DeSerializer.toList(levelBytes, Wall.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.exception(e);
		}
	}
	
	protected void createPowerup() {
		// chance of power up happening is 1/100 [possibly too much?]
		
		if ((int) Math.ceil(random.nextDouble() * 100) == Powerup.LUCKY_POWERUP_NUMBER) {
			Powerup curr = getNewPowerup();
			powerups.add(curr);
		}
		
	}


	protected Powerup getNewPowerup() {
		Powerup newPowerup;
		do {
			final double xNew = random.nextDouble();
			final double yNew = random.nextDouble();
			newPowerup = new Powerup(xNew, yNew, Powerup.POWERUP_HALF_DAMAGE);
			
			//Power up shouldn't spawn inside a wall
		} while (isPowerupInsideAnyWall(newPowerup));
		
		return newPowerup;
	}
	
	private void updatePowerups() {
		
		final Iterator<Powerup> powerupIterator = powerups.iterator();
		while (powerupIterator.hasNext()) {
			Powerup powerup = powerupIterator.next();
			
			// Check if tank has collected the power up
			if (isPowerupCollected(powerup)) {
				powerupIterator.remove();
			}
			
			// If a tank hasn't taken a power up, decrease its time alive
			powerup.timeAlive--;
			if (powerup.timeAlive == 0) {
				powerupIterator.remove();
			}
		} 
	}

	protected boolean isPowerupCollected(Powerup powerup) {
		final Point2D.Double powerupLoc = new Point2D.Double(powerup.x * Tank.SCALAR, powerup.y * Tank.SCALAR);

		final Iterator<Tank> tankIterator = tanks.iterator();
		while (tankIterator.hasNext()) {
			Tank tank = tankIterator.next();
			final Polygon tankPolygon = tank.getTankRectangle();
			
			if (tankPolygon.contains(powerupLoc)) {
				tank.powerups.add(new Powerup(0, 0, Powerup.randomizeType(random)));
				return true;
			}
			
		}
		return false;
	}

	protected List<Wall> wallsFromMatrix(boolean[][] levelMatrix){
		List<Wall> level  = new ArrayList<Wall>();
		double wallHeight = 1/(double) levelMatrix[0].length;
		double wallWidth  = 1/(double) levelMatrix.length;//BasicClient.MENU_WIDTH /levelMatrix.length;
		
		for (int i = 0; i < levelMatrix.length; i++) {
			for (int j = 0; j < levelMatrix[i].length; j++) {
				if (levelMatrix[i][j]) {
					level.add(new Wall(wallWidth*j, wallHeight*i,wallHeight,wallWidth));
				}
			}
		}
		
		return level;
	}
	
	public void update(Input[] inputs) {

		for (int i = 0; i < tanks.size(); i++) {
			final Tank tank = tanks.get(i);
			
			//update tanks power ups
			tank.updatePowerups();
			
			Input currInput = inputs[tank.id];

			// Update gun angle before shooting or moving
			updateGunAngle(tank, currInput.x, currInput.y);

			// Angle tank before moving
			if (currInput.a == true) {
				angleTank(tank, false);
			}
			if (currInput.d == true) {
				angleTank(tank, true);
			}

			// Move with new angle
			if (currInput.w == true) {
				moveTank(tank, true);
			}
			if (currInput.s == true) {
				moveTank(tank, false);
			}
		}

		// Create bullet
		for (int i = 0; i < tanks.size(); i++) {
			final Tank tank = tanks.get(i);
			final Input currInput = inputs[tank.id];

			tank.timeBeforeShoot--;
			if (currInput.click == true && tank.canShoot()) {
				bullets.add(tank.shoot());
			}
		}

		// Update the locations of the bullets and decide if any hit
		final Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			final Bullet bullet = bulletIterator.next();

			bullet.timeAlive--;
			if (!bullet.stillAlive()) {
				bulletIterator.remove();
			}

			if (!updateBulletLocation(bullet)) {
				bulletIterator.remove();
			}
		}
		
		//Create power ups
		createPowerup();
		
		//Update power ups
		updatePowerups();
	}

	// returns false if bullet must be deleted
	protected Boolean updateBulletLocation(Bullet bullet) {
		bullet.move();

		final boolean isBulletDead = Ricochet.simpleBounce(bullet, walls);
		if (isBulletDead) {
			return false;
		}

		return !checkDamage(bullet);
	}

	// returns true if bullet hits
	protected Boolean checkDamage(Bullet bullet) {
		final Point2D.Double bulletPos = new Point2D.Double(bullet.x * Tank.SCALAR, bullet.y * Tank.SCALAR);

		final Iterator<Tank> tankIterator = tanks.iterator();
		while (tankIterator.hasNext()) {
			Tank tank = tankIterator.next();
			final Polygon tankPolygon = tank.getTankRectangle();

			if (tankPolygon.contains(bulletPos)) {
				if (tank.hasPowerup(Powerup.POWERUP_HALF_DAMAGE)) {
					tank.takeDamage((int) Math.floor(bullet.bulletDamage * 0.5));
				} else {
					tank.takeDamage(bullet.bulletDamage);
				}
				if (!tank.isAlive()) {
					tankIterator.remove();
				}
				return true;
			}

		}
		return false;
	}

	protected void updateGunAngle(Tank currTank, double pointerX, double pointerY) {
		final double x = pointerX - currTank.x;
		final double y = pointerY - currTank.y;

		currTank.gunAngle = Math.atan2(y, x);
	}

	// true: clockwise, false: counterclockwise
	protected void angleTank(Tank currTank, boolean angle) {
		final double angleAddition = angle ? Math.toRadians(Tank.TURNING_ANGLE) : -Math.toRadians(Tank.TURNING_ANGLE);

		currTank.bodyAngle += angleAddition;
		if (isTankInsideAnyWall(currTank)) {
			currTank.bodyAngle -= angleAddition;
		}
	}

	// true: forward, false: backward
	protected void moveTank(Tank currTank, Boolean direction) {
		final double x = Math.cos(currTank.bodyAngle) * Tank.TANK_MOVEMENT_DISTANCE * (direction ? -1 : 1);
		final double y = Math.sin(currTank.bodyAngle) * Tank.TANK_MOVEMENT_DISTANCE * (direction ? -1 : 1);
		currTank.x += x;
		if (isTankInsideAnyWall(currTank)) {
			currTank.x -= x;
		}

		currTank.y += y;
		if (isTankInsideAnyWall(currTank)) {
			currTank.y -= y;
		}
	}
	

	protected boolean isTankInsideAnyWall(Tank tank) {
		for (Wall wall : walls) {
			if (wall.collidesWith(tank)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isPowerupInsideAnyWall(Powerup powerup)
	{
		for (Wall wall : walls) {
			if (wall.collidesWith(powerup)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Tank> getTanks() {
		return tanks;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
}