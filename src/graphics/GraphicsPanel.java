package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import javax.swing.JPanel;

import engine.Bullet;
import engine.Tank;
import engine.Wall;

public class GraphicsPanel extends JPanel {
	private ArrayList<Tank> tanks = new ArrayList<Tank>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	
	public GraphicsPanel() {
		setBackground(Color.WHITE);
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawTanks(g);
		drawBullets(g);
		drawWalls(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		int min = Math.min(this.getParent().getWidth(), this.getParent().getHeight());
	    return new Dimension(min, min);
	}
	
	private void drawTanks(Graphics g)
	{
		g.setColor(Color.GREEN);
		for (Tank tank : tanks) {
			if (!tank.isAlive()) {
				continue;
			}
			drawTankBody(tank, g);
		}
		
		g.setColor(Color.BLACK);
		for (Tank tank : tanks) {
			if (!tank.isAlive()) {
				continue;
			}
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
		Polygon gunBody = tank.getGunRectangle(this.getWidth(), this.getHeight());
		g.fillPolygon(gunBody);
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
		final int x = (int)((bullet.x - bullet.width / 2) * this.getWidth());
		final int y = (int)((bullet.y - bullet.height / 2) * this.getHeight());
		final int width = (int)(bullet.width * this.getWidth());
		final int height = (int)(bullet.height * this.getHeight());
		
		g.fillOval(x, y, width, height);
	}
	
	private void drawWalls(Graphics g)
	{
		g.setColor(Color.GRAY);
		for (Wall wall : walls) {
			drawWall(wall, g);
		}
	}
	
	private void drawWall(Wall wall, Graphics g)
	{
		final int x = (int)((wall.x) * this.getWidth());
		final int y = (int)((wall.y) * this.getHeight());
		final int width = (int)(wall.width * this.getWidth());
		final int height = (int)(wall.height * this.getHeight());
		
		g.fillRect(x, y, width, height);
	}
	
	public void setTanks(ArrayList<Tank> tanks)
	{
		this.tanks = tanks;
	}
	
	public void setBullets(ArrayList<Bullet> bullets)
	{
		this.bullets = bullets;
	}
	
	public void setWalls(ArrayList<Wall> walls)
	{
		this.walls = walls;
	}
	
}