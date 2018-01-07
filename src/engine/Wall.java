package engine;

import java.awt.Polygon;
import java.awt.geom.Line2D;

public class Wall {
	public final double x;
	public final double y;
	public final double width;
	public final double height;
	public final Line2D.Double topLine;
	public final Line2D.Double bottomLine;
	public final Line2D.Double leftLine;
	public final Line2D.Double rightLine;
	
	
	public Wall(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.topLine    = new Line2D.Double(x        , y         , x + width, y         );
		this.bottomLine = new Line2D.Double(x        , y + height, x + width, y + height);
		this.leftLine   = new Line2D.Double(x        , y         , x        , y + height);
		this.rightLine  = new Line2D.Double(x + width, y         , x + width, y + height);
	}
	
	public boolean contains(double pX, double pY)
	{
		return x < pX && pX < x + width &&
			   y < pY && pY < y + height;
	}
	
	public boolean collidesWith(Tank tank)
	{
		final Polygon tankBox = tank.getTankRectangle();
		return tankBox.intersects(x * Tank.SCALAR, y * Tank.SCALAR, width * Tank.SCALAR, height * Tank.SCALAR);
	}
}
