package engine;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class Tank {
	public double x;
	public double y;
	public double bodyWidth;
	public double bodyHeight;
	public double bodyAngle;
	public double gunAngle;
	int id;
	int health;
	
	public static final double TANK_WIDTH = 0.05;
	public static final double TANK_HEIGHT = 0.05;
	public static final double GUN_WIDTH = 0.07;
	public static final double GUN_HEIGHT = 0.005;
	public static final int TANK_HEALTH = 100;
	public static final double SCALAR = 10000;
	
	public Tank(double xNew, double yNew, 
			double bodyAngleNew, double gunAngleNew, int idNew) {
		x = xNew;
		y = yNew;
		bodyWidth = TANK_WIDTH;
		bodyHeight = TANK_HEIGHT;
		bodyAngle = bodyAngleNew;
		gunAngle = gunAngleNew;
		id = idNew;
		health = TANK_HEALTH;
	}
	
	public void takeDamage(int damage) {
		if (damage > health) {
			health = 0;
		} else {
			health -= damage;
		}
	}
	
	public Point2D.Double getBulletStartPos()
	{
		final double startX = x + Math.cos(gunAngle) * Tank.GUN_WIDTH * 0.6;
		final double startY = y + Math.sin(gunAngle) * Tank.GUN_WIDTH * 0.6;
		
		return new Point2D.Double(startX,  startY);
	}
	
	
	public Polygon getTankRectangle()
	{
		return getTankRectangle(Tank.SCALAR, Tank.SCALAR);
	}
	
	public Polygon getTankRectangle(double xScalar, double yScalar)
	{
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
	
	public Polygon getGunRectangle(double xScalar, double yScalar)
	{
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
	
	private Point rotateMoveScale(final double cornerX, final double cornerY, final double angle, final double xScalar, final double yScalar)
	{
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
	
	private double rotateX(double x, double y, double angle)
	{
		return x * Math.cos(angle) - y * Math.sin(angle);
	}
	
	private double rotateY(double x, double y, double angle)
	{
		return x * Math.sin(angle) + y * Math.cos(angle);
	}
}
