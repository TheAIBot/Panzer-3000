package engine;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Wall extends DeSerializer {
	public double x;
	public double y;
	public double width;
	public double height;
	public Line2D.Double topLine;
	public Line2D.Double bottomLine;
	public Line2D.Double leftLine;
	public Line2D.Double rightLine;
	
	
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
	
	public Wall() {
		
	}

	@Override
	protected void toBytes(DataOutputStream out) throws IOException {
		out.writeFloat((float) x);
		out.writeFloat((float) y);
		out.writeFloat((float) width);
		out.writeFloat((float) height);
	}

	@Override
	protected void fromBytes(DataInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
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
