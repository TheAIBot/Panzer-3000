package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import engine.entities.Bullet;
import engine.entities.Powerup;
import engine.entities.Tank;
import engine.entities.Wall;

public class GraphicsPanel extends JPanel {
	private ArrayList<Tank>   tanks 	= new ArrayList<Tank>();
	private ArrayList<Bullet> bullets 	= new ArrayList<Bullet>();
	private ArrayList<Powerup>powerups 	= new ArrayList<Powerup>();
	private ArrayList<Wall>   walls 	= new ArrayList<Wall>();
	public boolean playerHasWon = false;
	
	public GraphicsPanel() {
		setBackground(Color.WHITE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                		   				  RenderingHints.VALUE_ANTIALIAS_ON);

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING,
                		   				  RenderingHints.VALUE_RENDER_SPEED);
		super.paintComponent(g);
		synchronized (this) {
			drawTanks(g);
			drawBullets(g);
			drawPowerups(g);
			drawWalls(g);
			drawWinnerMessage(g);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		final int min = Math.min(this.getParent().getWidth(), this.getParent().getHeight());
	    return new Dimension(min, min);
	}

	
	private void drawWinnerMessage(Graphics g) {
		if (playerHasWon) {
			String message;
			if (tanks.size() == 0) { //Initially zero
				message = "切腹";
			} else if (tanks.size() == 1) {
				message = "Player " + tanks.get(0).userName + " has won.";
			} else {
				throw new Error("The game has ended with a number of tanks alive, different from 0 or 1");
			}
			
			//find a font size that allows the whole sring to be shown
			Font sizedFont;
			FontMetrics metrics;
			int fontSize = 210;
			final String fontName = g.getFont().getFontName();
			do {
				fontSize -= 10;
				sizedFont = new Font(fontName, Font.PLAIN, fontSize);
				metrics = g.getFontMetrics(sizedFont);
			} while (metrics.stringWidth(message) > this.getWidth() && fontSize > 10);
			
			//start position of the string
			final int xCoordinate = (this.getWidth() / 2) - metrics.stringWidth(message) / 2;
			final int yCoordinate = (this.getHeight() / 2);
			
			//draw string
			g.setFont(sizedFont); 
			g.setColor(Color.BLACK);
			g.drawString(message, xCoordinate, yCoordinate);
			
		}		
	}
	
	private void drawTanks(Graphics g) {
		g.setColor(Color.GREEN);
		for (Tank tank : tanks) {
			drawTankBody(tank, g);
		}
		
		g.setColor(Color.RED);
		for (Tank tank : tanks) {
			drawTankHealth(tank, g);
		}
		
		g.setColor(Color.BLACK);
		for (Tank tank : tanks) {
			drawTankGun(tank, g);
		}
		
		for (Tank tank : tanks) {
			drawUserName(tank, g);
		}
	}

	private void drawPowerups(Graphics g) {
		g.setColor(Color.CYAN);
		for(Powerup powerup : powerups){
			drawPowerup(powerup, g);
		}
	}
	
	private void drawPowerup(Powerup powerup, Graphics g) {
		final int x = (int)((powerup.x - Powerup.POWERUP_WIDTH / 2) * this.getWidth());
		final int y = (int)((powerup.y - Powerup.POWERUP_HEIGHT / 2) * this.getHeight());
		final int width = (int)(Powerup.POWERUP_WIDTH * this.getWidth());
		final int height = (int)(Powerup.POWERUP_HEIGHT * this.getHeight());
		
		g.fillOval(x, y, width, height);
		
	}

	private void drawTankHealth(Tank tank, Graphics g) {
		g.fillPolygon(tank.getHealthBar(this.getWidth(), this.getHeight()));
	}
	
	private void drawUserName(Tank tank, Graphics g) {
	    final FontMetrics metrics = g.getFontMetrics(g.getFont());
		final int xCoordinate = (int) (tank.x * this.getWidth() - metrics.stringWidth(tank.userName)/2);
		final int yCoordinate = (int) ((tank.y - 0.7*tank.bodyHeight) * this.getHeight());
		g.drawString(tank.userName, xCoordinate, yCoordinate);
	}

	private void drawTankBody(Tank tank, Graphics g)
	{
		g.fillPolygon(tank.getTankRectangle(this.getWidth(), this.getHeight()));
	}
	
	private void drawTankGun(Tank tank, Graphics g)
	{
		g.fillPolygon(tank.getGunRectangle(this.getWidth(), this.getHeight()));
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
		final int x = (int)((bullet.x - bullet.size / 2) * this.getWidth());
		final int y = (int)((bullet.y - bullet.size / 2) * this.getHeight());
		final int width = (int)(bullet.size * this.getWidth());
		final int height = (int)(bullet.size * this.getHeight());
		
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
		final int width = (int)Math.ceil(wall.width * this.getWidth());
		final int height = (int)Math.ceil(wall.height * this.getHeight());
		
		g.fillRect(x, y, width, height);
	}
	
	public synchronized void setTanks(ArrayList<Tank> tanks)
	{
		this.tanks = new ArrayList<Tank>(tanks);
	}
	
	public synchronized void setBullets(ArrayList<Bullet> bullets)
	{
		this.bullets = new ArrayList<Bullet>(bullets);
	}

	public synchronized void setPowerups(ArrayList<Powerup> powerups) {
		this.powerups = new ArrayList<Powerup>(powerups);
	}
	
	public synchronized void setWalls(ArrayList<Wall> walls)
	{
		this.walls = new ArrayList<Wall>(walls);
	}

	public void setPlayerHasWon() {
		playerHasWon = true;
	}
	
	public void resetGraphics() {
		playerHasWon = false;
	}

	
}