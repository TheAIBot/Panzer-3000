package engine;

import connector.BasicClient;
import connector.ServerConnector;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Logger.Log;

public class GameEngine {
	ServerConnector connection;
	ArrayList<Tank> tanks = new ArrayList<Tank>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Wall> walls = new ArrayList<Wall>();
	public static final int FPS = 60;
	public static final double BOARD_MAX_X = 1;
	public static final double BOARD_MAX_Y = 1;
	public static boolean gameIsWon = false;
	public static final boolean LOAD_LEVEL = true;
	public static final String LEVEL_NAME = "basic";
	public static final String LEVEL_DIRECTORY = "src/levels/";

	public void startGame(int tankCount, String ipAddress, String[] usernames) {
		try {
			Log.message("Starting server");
			initializeWalls();
			initializeTanks(tankCount);
			connection = new ServerConnector();
			connection.initializeServerConnection(usernames.length, ipAddress, usernames);
			connection.setUserNames(tanks, usernames);
			Log.message("Clients connected");

			// The server will send the initial information first, such that the clients
			// have something to display:

			connection.sendWalls(walls);
			connection.sendUpdates(tanks, bullets);
			Log.message("Sent first update");

			Thread.sleep(2000);

			// Then the main loop can begin:

			while (true) { // Game loop
				final long startTime = System.currentTimeMillis();
				Input[] userInputs = connection.reciveUserInputs();
				// Log.message(userInputs[0].toString());
				// Log.message("Received inputs from clients");
				update(userInputs);

				// Log.message("Updated game");
				connection.sendUpdates(tanks, bullets);
				// Log.message("Sent game state update");
				if (hasTankWonGame(tanks, tankCount)) {
					// Victory!!!
					System.out.println("The game has been won!!!");
					break;
				}
				final long timePassed = System.currentTimeMillis() - startTime;
				final long timeToSleep = Math.max(0, (1000 / FPS) - timePassed);
				Thread.sleep(timeToSleep);
			}
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	public static boolean hasTankWonGame(ArrayList<Tank> tanks, int numberOfClients) {
		return tanks.size() <= 1 && tanks.size() != numberOfClients;
	}

	private void initializeTanks(int tankCount) {
		for (int i = 0; i < tankCount; i++) {
			Tank newTank;

			do {
				final double xNew = Math.random();
				final double yNew = Math.random();
				newTank = new Tank(xNew, yNew, 0, 0, i);
				// tank shouldn't spawn inside a wall
			} while (isTankInsideAnyWall(newTank));

			tanks.add(newTank);
		}
	}

	private void initializeWalls() {
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
				walls.add(new Wall(Math.random(), Math.random(), 0.1, 0.1));
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


	private List<Wall> wallsFromMatrix(boolean[][] levelMatrix){
		List<Wall> level = new ArrayList<Wall>();
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
	}

	// returns false if bullet must be deleted
	private Boolean updateBulletLocation(Bullet bullet) {
		bullet.move();

		final boolean isBulletDead = Ricochet.simpleBounce(bullet, walls);
		if (isBulletDead) {
			return false;
		}

		return !checkDamage(bullet);
	}

	// returns true if bullet hits
	private Boolean checkDamage(Bullet bullet) {
		final Point2D.Double bulletPos = new Point2D.Double(bullet.x * Tank.SCALAR, bullet.y * Tank.SCALAR);

		final Iterator<Tank> tankIterator = tanks.iterator();
		while (tankIterator.hasNext()) {
			Tank tank = tankIterator.next();
			final Polygon tankPolygon = tank.getTankRectangle();

			if (tankPolygon.contains(bulletPos)) {
				tank.takeDamage(Bullet.BULLET_DAMAGE);
				if (!tank.isAlive()) {
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

	// true: clockwise, false: counterclockwise
	private void angleTank(Tank currTank, boolean angle) {
		final double angleAddition = angle ? Math.toRadians(Tank.TURNING_ANGLE) : -Math.toRadians(Tank.TURNING_ANGLE);

		currTank.bodyAngle += angleAddition;
		if (isTankInsideAnyWall(currTank)) {
			currTank.bodyAngle -= angleAddition;
		}
	}

	// true: forward, false: backward
	private void moveTank(Tank currTank, Boolean direction) {
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
	

	private boolean isTankInsideAnyWall(Tank tank) {
		for (Wall wall : walls) {
			if (wall.collidesWith(tank)) {
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
