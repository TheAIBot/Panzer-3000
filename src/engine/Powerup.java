package engine;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import logger.Log;

public class Powerup extends DeSerializer {
	public double x;
	public double y;
	public int timeAlive;
	public int type;
	public static final double POWERUP_WIDTH = 0.05;
	public static final double POWERUP_HEIGHT = 0.05;
	public static final int POWERUP_TIME_ALIVE = 500;
	
	public static final int LUCKY_POWERUP_NUMBER = 1;
	public static final int NUM_POWERUPS = 2;
			
	public static final int POWERUP_HALF_DAMAGE = 1;
	public static final int POWERUP_DOUBLE_DAMAGE = 2;
	
	
	public Ellipse2D getEllipse(double xScalar, double yScalar) {
		return new Ellipse2D.Double(x * xScalar * yScalar, y, POWERUP_WIDTH * xScalar, POWERUP_HEIGHT * yScalar);
	}
	
	public Polygon getPowerupRectangle(double xScalar, double yScalar){
		//create the tank corners
		int left = 	 (int) (xScalar*(x - POWERUP_WIDTH  / 2));
		int right =  (int) (xScalar*(x + POWERUP_WIDTH  / 2));
		int top = 	 (int) (yScalar*(y + POWERUP_WIDTH  / 2));
		int bottom = (int) (yScalar*(y - POWERUP_WIDTH  / 2));
		
		final Point topLeft     = new Point(left, top);
		final Point topRight    = new Point(right, top);
		final Point bottomLeft  = new Point(left, bottom);
		final Point bottomRight = new Point(right, bottom);
		
		//put corners into a polygon
		int[] xPoints =  {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
		int[] yPoints =  {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};	
		return new Polygon(xPoints, yPoints, 4);
	}
	
	public Powerup(double x, double y, int type) {
		this.x = x;
		this.y = y;
		this.timeAlive = POWERUP_TIME_ALIVE;
		this.type = type;
	}
	
	public Powerup() {
		
	}

	@Override
	protected void toBytes(DataOutputStream out) throws IOException {
		out.writeFloat((float) x);
		out.writeFloat((float) y);
		
	}

	@Override
	protected void fromBytes(DataInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		
	}

	public static int randomizeType() {
		return(int) Math.ceil(Math.random() * NUM_POWERUPS);
	}

}
