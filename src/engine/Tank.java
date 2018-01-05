package engine;

import java.awt.Point;
import java.awt.Polygon;

public class Tank {
	public double x;
	public double y;
	public double bodyWidth;
	public double bodyHeight;
	public double bodyAngle;
	public double gunAngle;
	int id;
	
	public Tank(double xNew, double yNew, double bodyWidthNew, double bodyHeightNew, 
			double bodyAngleNew, double gunAngleNew, int idNew) {
		x = xNew;
		y = yNew;
		bodyWidth = bodyWidthNew;
		bodyHeight = bodyHeightNew;
		bodyAngle = bodyAngleNew;
		gunAngle = gunAngleNew;
		id = idNew;
	}
	
	public static final double SCALAR = 10000;
	
	public Polygon getTankRectangle()
	{
		return getTankRectangle(Tank.SCALAR, Tank.SCALAR);
	}
	
	public Polygon getTankRectangle(double xScalar, double yScalar)
	{
		//create the tank corners
		final Point topLeft     = rotateMoveScale(-bodyWidth  / 2, -bodyHeight / 2, xScalar, yScalar);
		final Point topRight    = rotateMoveScale( bodyWidth  / 2, -bodyHeight / 2, xScalar, yScalar);
		final Point bottomLeft  = rotateMoveScale(-bodyWidth  / 2,  bodyHeight / 2, xScalar, yScalar);
		final Point bottomRight = rotateMoveScale( bodyWidth  / 2,  bodyHeight / 2, xScalar, yScalar);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	private Point rotateMoveScale(final double cornerX, final double cornerY, final double xScalar, final double yScalar)
	{
		//rotates the point around (0, 0)
		final double rotatedX = rotateX(cornerX, cornerY, bodyAngle);
		final double rotatexY = rotateY(cornerX, cornerY, bodyAngle);
		
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
