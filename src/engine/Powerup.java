package engine;

import java.awt.geom.Ellipse2D;

public class Powerup {
	double x;
	double y;
	public int timeAlive;
	public int type;
	public static final double POWERUP_WIDTH = 0.05;
	public static final double POWERUP_HEIGHT = 0.05;
	public static final int POWERUP_TIME_ALIVE = 170;
	
	public Ellipse2D getEllipse() {
		return new Ellipse2D.Double(x, y, POWERUP_WIDTH, POWERUP_HEIGHT);
	}
	
	public Powerup(double x, double y, int type) {
		this.x = x;
		this.y = y;
		this.timeAlive = POWERUP_TIME_ALIVE;
		this.type = type;
	}

}
