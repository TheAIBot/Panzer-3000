package tests;

import engine.Bullet;
import engine.GameEngine;
import engine.Input;
import engine.Tank;

import org.junit.*;

public class gameEngineTests {


	public static final int TEST_TANK_COUNT = 2;
	GameEngine gameEngine;
	
	@Test
	public void startupTest() {
		gameEngine = new GameEngine();
		gameEngine.initializeTanksBullets(TEST_TANK_COUNT);
		org.junit.Assert.assertTrue(gameEngine != null);
		org.junit.Assert.assertTrue(gameEngine.getTanks() != null);
		org.junit.Assert.assertTrue(gameEngine.getTanks().size() == TEST_TANK_COUNT);
		org.junit.Assert.assertTrue(gameEngine.getBullets().size() == 0);
		
		for (Tank tank : gameEngine.getTanks()) {
			tankBasicTest(tank, 0, 0);
		}
		
	}
	
	@Test
	public void gunAngleTest() {
		gameEngine = new GameEngine();
		gameEngine.initializeTanksBullets(TEST_TANK_COUNT);
		Input inputs[] = new Input[1];
		
		// Pointer is at the right of screen, in line with tank
		for(Tank tank : gameEngine.getTanks()) {
			Input newInput = new Input(false, false, false,  false, false, 1, tank.y, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			tankBasicTest(tank, 0, Math.toRadians(90));
		}

		// Pointer is at the top of screen, in line with tank
		for(Tank tank : gameEngine.getTanks()) {
			Input newInput = new Input(false, false, false,  false, false, tank.x, 1, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			tankBasicTest(tank, 0, 0);
		}

		// Pointer is at a 45 degree angle to the tank in the negative direction
		for(Tank tank : gameEngine.getTanks()) {
			double x = 0;
			double y = tank.y - tank.x;
			
			if (tank.x > tank.y) {
				x = tank.x - tank.y;
				y = 0;
			}
			Input newInput = new Input(false, false, false,  false, false, x, y, tank.getId());
			
			inputs[0] = newInput;
			gameEngine.update(inputs);
			tankBasicTest(tank, 0, Math.toRadians(225));
		}
	}
	
	@Test
	public void moveTest() {
		gameEngine = new GameEngine();
		gameEngine.initializeTanksBullets(TEST_TANK_COUNT);
		Input inputs[] = new Input[1];
		
		for(Tank tank : gameEngine.getTanks()) {
			double tankInitX = tank.x;
			double tankInitY = tank.y;
			double tankBodyAngle = tank.bodyAngle;
			double tankGunAngle = tank.gunAngle;
			Input newInput = new Input(true, false, false,  false, false, tank.x, tank.y, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			tankBasicTest(tank, tankBodyAngle, tankGunAngle);
			org.junit.Assert.assertTrue(tank.x == tankInitX);
			org.junit.Assert.assertTrue(tank.y == tankInitY + GameEngine.TANK_MOVEMENT_DISTANCE);
		}
		
		for(Tank tank : gameEngine.getTanks()) {
			double tankInitX = tank.x;
			double tankInitY = tank.y;
			double tankBodyAngle = tank.bodyAngle;
			double tankGunAngle = tank.gunAngle;
			Input newInput = new Input(false, false, true,  false, false, tank.x, tank.y, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			tankBasicTest(tank, tankBodyAngle, tankGunAngle);
			org.junit.Assert.assertTrue(tank.x == tankInitX);
			org.junit.Assert.assertTrue(tank.y == tankInitY - GameEngine.TANK_MOVEMENT_DISTANCE);
		}
	}
	
	
	@Test
	public void bodyAngleTest() {
		gameEngine = new GameEngine();
		gameEngine.initializeTanksBullets(TEST_TANK_COUNT);
		Input inputs[] = new Input[1];
		
		for(Tank tank : gameEngine.getTanks()) {
			double tankBodyAngle = tank.bodyAngle;
			double tankGunAngle = tank.gunAngle;
			Input newInput = new Input(false, true, false,  false, false, tank.x, tank.y, tank.getId());
			inputs[0] = newInput;
			System.out.println(tank.getId() + "Input: " + inputs[0].id);
			gameEngine.update(inputs);
			Double newAngle = (tankBodyAngle + GameEngine.TANK_ROTATION_ANGLE) % Math.toRadians(360);
			if (newAngle < 0) {
				newAngle = Math.toRadians(360) + newAngle;
			}
			System.out.println("Starting test");
			tankBasicTest(tank, newAngle, tankGunAngle);
			System.out.println("finishing test");
		}
		
		for(Tank tank : gameEngine.getTanks()) {
			double tankBodyAngle = tank.bodyAngle;
			double tankGunAngle = tank.gunAngle;
			Input newInput = new Input(false, false, false,  true, false, tank.x, tank.y, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			Double newAngle = (tankBodyAngle + GameEngine.TANK_ROTATION_ANGLE) % Math.toRadians(360);
			if (newAngle < 0) {
				newAngle = Math.toRadians(360) + newAngle;
			}
			tankBasicTest(tank, newAngle, tankGunAngle);
		}
	}
	
	@Test
	public void bulletTest() {
		gameEngine = new GameEngine();
		gameEngine.initializeTanksBullets(TEST_TANK_COUNT);
		Input inputs[] = new Input[1];
		
		// Pointer is at the right of screen, in line with tank
		for(Tank tank : gameEngine.getTanks()) {
			Input newInput = new Input(false, false, false,  false, true, tank.x, tank.y, tank.getId());
			inputs[0] = newInput;
			gameEngine.update(inputs);
			Bullet newBullet = gameEngine.getBullets().get(gameEngine.getBullets().size() - 1);
			org.junit.Assert.assertTrue(newBullet.angle == tank.gunAngle);
			org.junit.Assert.assertTrue(newBullet.x == tank.x);
			org.junit.Assert.assertTrue(newBullet.y == tank.y);
			org.junit.Assert.assertTrue(newBullet.width == GameEngine.BULLET_WIDTH);
			org.junit.Assert.assertTrue(newBullet.height == GameEngine.BULLET_HEIGHT);
		}

	}
	
	public void tankBasicTest(Tank tank, double bodyAngle, double gunAngle) {
		org.junit.Assert.assertTrue(tank.x < GameEngine.BOARD_MAX_X);
		org.junit.Assert.assertTrue(tank.y < GameEngine.BOARD_MAX_Y);
		org.junit.Assert.assertTrue(tank.x > 0);
		org.junit.Assert.assertTrue(tank.y > 0);
		org.junit.Assert.assertTrue(tank.bodyWidth == GameEngine.TANK_WIDTH);
		org.junit.Assert.assertTrue(tank.bodyHeight == GameEngine.TANK_HEIGHT);
		System.out.println("Tank body angle: " + Math.toDegrees(tank.bodyAngle));
		org.junit.Assert.assertTrue(tank.bodyAngle == bodyAngle);
		org.junit.Assert.assertTrue(tank.gunAngle == gunAngle);
		org.junit.Assert.assertTrue(tank.getId() < TEST_TANK_COUNT);
	}
}



