package engine;

import java.awt.geom.Ellipse2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
	
	public Ellipse2D getEllipse() {
		return new Ellipse2D.Double(x, y, POWERUP_WIDTH, POWERUP_HEIGHT);
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
