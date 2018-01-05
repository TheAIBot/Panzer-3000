package engine;

import java.awt.Point;
import java.awt.Polygon;

public class Bullet {
	public double x;
	public double y;
	public double width;
	public double height;
	public double angle;
	public static final int BULLET_DAMAGE = 10;
	
	public Bullet(double xNew, double yNew, double widthNew, double heightNew, double angleNew) {
		x = xNew;
		y = yNew;
		width = widthNew;
		height = heightNew;
		angle = angleNew;
	}
	

	public static final double SCALAR = 10000;
	
	public Polygon getBulletRectangle()
	{
		return getBulletRectangle(Tank.SCALAR, Tank.SCALAR);
	}
	
	public Polygon getBulletRectangle(double xScalar, double yScalar)
	{
		//create the tank corners
		final Point topLeft     = rotateMoveScale(-width  / 2, -height / 2, xScalar, yScalar);
		final Point topRight    = rotateMoveScale( width  / 2, -height / 2, xScalar, yScalar);
		final Point bottomLeft  = rotateMoveScale(-width  / 2,  height / 2, xScalar, yScalar);
		final Point bottomRight = rotateMoveScale( width  / 2,  height / 2, xScalar, yScalar);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	private Point rotateMoveScale(final double cornerX, final double cornerY, final double xScalar, final double yScalar)
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
