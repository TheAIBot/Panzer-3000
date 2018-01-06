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
}
