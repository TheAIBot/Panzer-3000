package engine;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Tank extends DeSerializer {
	public double x;
	public double y;
	public double bodyWidth;
	public double bodyHeight;
	public double bodyAngle;
	public double gunAngle;
	public int 	  id;
	int health;
	public int timeBeforeShoot = 0;
	public ArrayList<Powerup> powerups;
	public String userName;

	public static final double TANK_MOVEMENT_DISTANCE = 0.004;
	public static final double 	DEGREE 				= 2*Math.PI/360;
	public static final double 	TANK_WIDTH 			= 0.05;
	public static final double 	TANK_HEIGHT 		= 0.05;
	public static final double 	GUN_WIDTH 			= 0.07;
	public static final double 	GUN_HEIGHT 			= 0.005;
	public static final int 	TANK_HEALTH 		= 100;
	public static final double	SCALAR 				= 10000;
	public static final int 	TIME_BETWEEN_SHOTS 	= 10;
	public static final double 	TURNING_ANGLE 		= 1.5;
	
	public Tank(double xNew, double yNew, double bodyAngleNew, double gunAngleNew, int idNew) {
		x = xNew;
		y = yNew;
		bodyWidth = TANK_WIDTH;
		bodyHeight = TANK_HEIGHT;
		bodyAngle = bodyAngleNew;
		gunAngle = gunAngleNew;
		id = idNew;
		health = TANK_HEALTH;
		powerups = new ArrayList<Powerup>();
	}
	
	public void updatePowerups() {

		final Iterator<Powerup> powerupIterator = powerups.iterator();
		while (powerupIterator.hasNext()) {
			Powerup powerup = powerupIterator.next();
			powerup.timeAlive--;
		}
	}
	
	public Boolean hasPowerup(int powerupId) {

		final Iterator<Powerup> powerupIterator = powerups.iterator();
		while (powerupIterator.hasNext()) {
			Powerup powerup = powerupIterator.next();
			if (powerup.type == powerupId) {
				return true;
			}
		}
		return false;
	}
	
	public Tank() {
		
	}
	
	@Override
	protected void toBytes(DataOutputStream out) throws IOException {
		out.writeFloat((float) x);
		out.writeFloat((float) y);
		out.writeFloat((float) bodyWidth);
		out.writeFloat((float) bodyHeight);
		out.writeFloat((float) bodyAngle);
		out.writeFloat((float) gunAngle);
		out.writeInt(id);
		out.writeInt(health);
		out.writeInt(timeBeforeShoot);
		out.writeUTF(userName);
	}

	@Override
	protected void fromBytes(DataInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		bodyWidth = in.readFloat();
		bodyHeight = in.readFloat();
		bodyAngle = in.readFloat();
		gunAngle = in.readFloat();
		id = in.readInt();
		health = in.readInt();
		timeBeforeShoot = in.readInt();
		userName = in.readUTF();
		
	}
	
	public void takeDamage(int damage) {
		if (damage > health) {
			health = 0;
		} else {
			health -= damage;
		}
	}
	
	public Polygon getTankRectangle(){
		return getTankRectangle(Tank.SCALAR, Tank.SCALAR);
	}
	
	public Polygon getTankRectangle(double xScalar, double yScalar){
		//create the tank corners
		final Point topLeft     = rotateMoveScale(-bodyWidth  / 2, -bodyHeight / 2, bodyAngle, xScalar, yScalar);
		final Point topRight    = rotateMoveScale( bodyWidth  / 2, -bodyHeight / 2, bodyAngle, xScalar, yScalar);
		final Point bottomLeft  = rotateMoveScale(-bodyWidth  / 2,  bodyHeight / 2, bodyAngle, xScalar, yScalar);
		final Point bottomRight = rotateMoveScale( bodyWidth  / 2,  bodyHeight / 2, bodyAngle, xScalar, yScalar);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	public Polygon getHealthBar(double xScalar, double yScalar) {
		
		final double relativeHealth = health/((double) TANK_HEALTH);
		final double leftCoordinate = -bodyWidth / 2; 
		//Linear function from the right of the tank at full health, to the left of the tank at 0 health.
		final double rightCoordinate = (relativeHealth) * (bodyWidth  / 2) + (1 - relativeHealth) * leftCoordinate;
	
		
		//create health bar corners
		final Point topLeft     = rotateMoveScale( leftCoordinate ,  bodyHeight / 4, bodyAngle - 90 * DEGREE, xScalar, yScalar);
		final Point topRight    = rotateMoveScale( rightCoordinate,  bodyHeight / 4, bodyAngle - 90 * DEGREE, xScalar, yScalar);
		final Point bottomLeft  = rotateMoveScale( leftCoordinate ,  bodyHeight / 2, bodyAngle - 90 * DEGREE, xScalar, yScalar);
		final Point bottomRight = rotateMoveScale( rightCoordinate,  bodyHeight / 2, bodyAngle - 90 * DEGREE, xScalar, yScalar);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	public Polygon getGunRectangle(double xScalar, double yScalar){
		//create gun corners
		final Point topLeft     = rotateMoveScale(-GUN_WIDTH * 0.1 / 2, -GUN_HEIGHT / 2, gunAngle, xScalar, yScalar);
		final Point topRight    = rotateMoveScale( GUN_WIDTH * 1   / 2, -GUN_HEIGHT / 2, gunAngle, xScalar, yScalar);
		final Point bottomLeft  = rotateMoveScale(-GUN_WIDTH * 0.1 / 2,  GUN_HEIGHT / 2, gunAngle, xScalar, yScalar);
		final Point bottomRight = rotateMoveScale( GUN_WIDTH * 1   / 2,  GUN_HEIGHT / 2, gunAngle, xScalar, yScalar);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	private Point rotateMoveScale(final double cornerX, final double cornerY, final double angle, final double xScalar, final double yScalar){
		//rotates the point around (0, 0)
		final double rotatedX = rotateX(cornerX, cornerY, angle);
		final double rotatexY = rotateY(cornerX, cornerY, angle);
		
		//moves the point to the tank
		final double movedX = rotatedX + x;
		final double movedY = rotatexY + y;
		
		//scale point
		final int scaledX = (int)(movedX * xScalar);
		final int scaledY = (int)(movedY * yScalar);
		
		return new Point(scaledX, scaledY);
	}
	
	private double rotateX(double x, double y, double angle) {
		return x * Math.cos(angle) - y * Math.sin(angle);
	}
	
	private double rotateY(double x, double y, double angle) {
		return x * Math.sin(angle) + y * Math.cos(angle);
	}

	public boolean canShoot() {
		return timeBeforeShoot <= 0;
	}
	
	public Bullet shoot() {
		timeBeforeShoot = TIME_BETWEEN_SHOTS;
		final Point2D.Double bulletStartPos = getBulletStartPos();
		return new Bullet(bulletStartPos.x, bulletStartPos.y, Bullet.BULLET_SIZE, gunAngle);
	}
	
	private Point2D.Double getBulletStartPos() {
		final double startX = x + Math.cos(gunAngle) * Tank.GUN_WIDTH * 0.6;
		final double startY = y + Math.sin(gunAngle) * Tank.GUN_WIDTH * 0.6;
		
		return new Point2D.Double(startX,  startY);
	}

	public boolean isAlive() {
		return health > 0;
	}

}
