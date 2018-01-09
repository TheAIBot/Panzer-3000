package engine;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Bullet extends DeSerializer {
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
	
	public Bullet() {
		
	}
	
	@Override
	protected void toBytes(DataOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(oldX);
		out.writeDouble(oldY);
		out.writeDouble(width);
		out.writeDouble(height);
		out.writeDouble(angle);
		out.writeInt(timeAlive);
	}

	@Override
	protected void fromBytes(DataInputStream in) throws IOException {
		x = in.readDouble();
		y = in.readDouble();
		oldX = in.readDouble();
		oldY = in.readDouble();
		width = in.readDouble();
		height = in.readDouble();
		angle = in.readDouble();
		timeAlive = in.readInt();
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
