package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import engine.Bullet;
import engine.Tank;

public class GraphicsPanel extends JPanel {
	private ArrayList<Tank> tanks = new ArrayList<Tank>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawTanks(g);
		drawBullets(g);
	}
	
	private void drawTanks(Graphics g)
	{
		g.setColor(Color.GREEN);
		for (Tank tank : tanks) {
			drawTankBody(tank, g);
		}
		
		g.setColor(Color.BLACK);
		for (Tank tank : tanks) {
			drawTankGun(tank, g);
		}
	}
	
	private void drawTankBody(Tank tank, Graphics g)
	{
		Polygon tankBody = tank.getTankRectangle(this.getWidth(), this.getHeight());
		g.fillPolygon(tankBody);
	}
	
	private void drawTankGun(Tank tank, Graphics g)
	{
		
	}
	
	private void drawBullets(Graphics g)
	{
		g.setColor(Color.RED);
		for (Bullet bullet : bullets) {
			drawBullet(bullet, g);
		}
	}
	
	private void drawBullet(Bullet bullet, Graphics g)
	{
		g.drawOval((int)(bullet.x - bullet.width / 2), (int)(bullet.y- bullet.height / 2), (int)bullet.width, (int)bullet.height);
	}
	
	public void setTanks(ArrayList<Tank> tanks)
	{
		this.tanks = tanks;
	}
	
	public void setBullets(ArrayList<Bullet> bullets)
	{
		this.bullets = bullets;
	}
	
}