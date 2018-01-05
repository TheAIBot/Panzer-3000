package engine;

public class GameEngine {
	//startGame(int tankCount);
	//update(Input[] inputs);
	//Tank[] getTanks();
	//Bullet[] getBullets();
	 public static final double BOARD_MAX_X = 1;
	 public static final double BOARD_MAX_Y = 1;
	 public static final double TANK_MOVEMENT_DISTANCE = 0.001;
	 public static final double BULLET_MOVEMENT_DISTANCE = 0.01;
	
	
	public void startGame(int tankCount) {
		
		//TODO: initialize connector
		while(true) {
			
			// TODO: GET ALL INPUTS
			update(null);
		}
	}
	
	public void update(Input[] inputs) {
		
		Tank[] currTanks = getTanks();
		Bullet[] currBullets = getBullets();

		
		for(int x = 0; x < inputs.length; x++) {
			Input currInput = inputs[x];
			
			//Deal with tank related inputs
			for(int y = 0; x < currTanks.length; x++) {
				if(currTanks[y].id == currInput.id) {

					//Update gun angle before shooting or moving
					updateGunAngle(currTanks[y], currInput.x, currInput.y);
					
					//Create bullet
					if(currInput.click == true) {
						createBullet(currTanks[y]);
					}
					
					// Angle tank before moving
					if(currInput.a == true) { angleTank(currTanks[y], false); }
					if(currInput.d == true) { angleTank(currTanks[y], true); }
					
					//Move with new angle
					if(currInput.w == true) { moveTank(currTanks[y], true); }
					if(currInput.s == true) { moveTank(currTanks[y], false); }
					
					
				}
			}
		}
		
		//Update the locations of the bullets and decide if any hit
		for (int x = 0; x < inputs.length; x++) {
			updateBulletLocation(currBullets[x]);
		}
		
	}
	
	private void updateBulletLocation(Bullet bullet) {
		
		// Use Pythagoras to work out changes in x and y given bullets angle and movement distance
		bullet.x += Math.sin( bullet.angle ) * BULLET_MOVEMENT_DISTANCE; 
		bullet.y += Math.cos( bullet.angle ) * BULLET_MOVEMENT_DISTANCE;
		
		checkDamage(bullet);
		
	}

	private void checkDamage(Bullet bullet) {
		// TODO Auto-generated method stub
		
		
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

		Bullet newBullet = new Bullet();
		newBullet.x = currTank.x;
		newBullet.y = currTank.y;
		newBullet.angle = currTank.gunAngle;
	}

	// true: clockwise, false: counterclockwise
	private void angleTank(Tank currTank, Boolean angle ) {
		if (angle) { currTank.bodyAngle += Math.toRadians(10); }
		else { currTank.bodyAngle -= Math.toRadians(10); }
	}

	// true: forward, false: backward
	private void moveTank(Tank currTank, Boolean direction) {
		if(direction) {
			currTank.x += Math.sin( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE; 
			currTank.y += Math.cos( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE;
		} else {
			currTank.x -= Math.sin( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE; 
			currTank.y -= Math.cos( currTank.bodyAngle ) * TANK_MOVEMENT_DISTANCE;
		}
	}

	public Tank[] getTanks() {
		//TODO: talk to connector
		
		return null;
		
	}
	
	public Bullet[] getBullets() {
		//TODO: talk to connector
		
		return null;
	
	}
	
}
