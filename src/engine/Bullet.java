package engine;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Bullet {
	public double x;
	public double y;
	public double oldX;
	public double oldY;
	public double width;
	public double height;
	public double angle;
	public int timeAlive;
	
	public static final double BULLET_WIDTH = 0.01;
	public static final double BULLET_HEIGHT = 0.01;
	public static final int BULLET_DAMAGE = 10;
	public static final double BULLET_MOVEMENT_DISTANCE = 0.01;
	public static final int BULLET_TIME_ALIVE = 170;
	
	public Bullet(double xNew, double yNew, double widthNew, double heightNew, double angleNew) {
		x = xNew;
		y = yNew;
		oldX = x;
		oldY = y;
		width = widthNew;
		height = heightNew;
		angle = angleNew;
		timeAlive = BULLET_TIME_ALIVE;
	}
	
	public void move()
	{
		oldX = x;
		oldY = y;
		final Point2D.Double nextPos = getNextPosition();
		x = nextPos.x; 
		y = nextPos.y;
	}
	
	public Point2D.Double getNextPosition()
	{
		final double newX = x + Math.cos(angle) * BULLET_MOVEMENT_DISTANCE; 
		final double newY = y + Math.sin(angle) * BULLET_MOVEMENT_DISTANCE;
		return new Point2D.Double(newX, newY);
	}
	
	public Line2D.Double getPath()
	{
		return new Line2D.Double(oldX, oldY, x, y);
	}
	
	public boolean stillAlive()
	{
		return timeAlive != 0;
	}
}
