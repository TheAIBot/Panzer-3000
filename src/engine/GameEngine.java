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
					if(currInput.a == true) { angleTank(currTanks[y].id, false); }
					if(currInput.d == true) { angleTank(currTanks[y].id, true); }
					
					//Move with new angle
					if(currInput.w == true) { moveTank(currTanks[y].id, true); }
					if(currInput.s == true) { moveTank(currTanks[y].id, false); }
					
					
				}
			}
		}
		
		//Update the locations of the bullets and decide if any hit
		for (int x = 0; x < inputs.length; x++) {
			updateBulletLocation(currBullets[x]);
		}
		
	}
	
	private void updateBulletLocation(Bullet bullet) {
		
		double x = 0;
		double y = 0;
		double angle = 0;
		double degreeAngle = Math.toDegrees(bullet.angle);
		
		// Use pythagoras to work out changes in x and y given bullets angle and movement distance
		if(degreeAngle < 90) {
			angle = degreeAngle;
			x = 1;
			y = 1; 
			
		} else if (degreeAngle < 180) {
			angle = degreeAngle - 90;
			x = 1;
			y = -1;
		
		} else if (degreeAngle < 270) {
			angle = degreeAngle - 180;
			x = -1;
			y = -1;
			
		} else {
			angle = degreeAngle - 270;
			x = -1;
			y = 1;
			
		}
		
		x = x * Math.sin( Math.toRadians(angle) ) * BULLET_MOVEMENT_DISTANCE; 
		y = y * Math.cos( Math.toRadians(angle) ) * BULLET_MOVEMENT_DISTANCE;
		
		bullet.x += x;
		bullet.y += y;
		
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
		
		if (x > 1 && y < 1) {
			radianAngle += Math.toRadians(90);
		} else if (x < 1 && y < 1) {
			radianAngle += Math.toRadians(180);
		} else if (x < 1 && y > 1) {
			radianAngle += Math.toRadians(270);
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
	private void angleTank(int id, Boolean direction) {
		// TODO Auto-generated method stub
		
	}

	// true: forward, false: backward
	private void moveTank(int id, Boolean angle) {
		// TODO Auto-generated method stub
		
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
