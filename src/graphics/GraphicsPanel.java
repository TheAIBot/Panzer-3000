package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import engine.Bullet;
import engine.Tank;

public class GraphicsPanel extends JPanel {
	ArrayList<Tank> tanks = new ArrayList<Tank>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawTanks(g);
		drawBullets(g);
	}
	
	private void drawTanks(Graphics g)
	{
		for (Tank tank : tanks) {
			drawTank(tank, g);
		}
	}
	
	private void drawTank(Tank tank, Graphics g)
	{
		
	}
	
	private void drawBullets(Graphics g)
	{
		for (Bullet bullet : bullets) {
			drawBullet(bullet, g);
		}
	}
	
	private void drawBullet(Bullet bullet, Graphics g)
	{
		g.setColor(Color.RED);
		g.drawOval((int)(bullet.x - bullet.width / 2), (int)(bullet.y- bullet.height / 2), (int)bullet.width, (int)bullet.height);
	}
	
}