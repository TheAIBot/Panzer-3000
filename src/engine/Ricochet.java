package engine;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Ricochet {
	
	public static boolean simpleBounce(Bullet bullet, ArrayList<Wall> walls)
	{
		final int angle = (int) Math.toDegrees(bullet.angle);
		final Line2D.Double bulletPath = bullet.getPath();
		boolean returnValue = true;
		
		for (int i = 0; i < walls.size(); i++) {
			final Wall wall = walls.get(i);
			
			if (wall.contains(bullet.x, bullet.y)) {
				if (wall.topLine.intersectsLine(bulletPath) ||
					wall.bottomLine.intersectsLine(bulletPath)) {
					bullet.angle = Math.toRadians(360 - angle);
				}
				else if (wall.leftLine.intersectsLine(bulletPath) ||
						 wall.rightLine.intersectsLine(bulletPath)) {
					int bouncedAngle = 180 - angle;
					//angle can't be less than 0 to make it positive
					if(bouncedAngle < 0)
					{
						bouncedAngle = bouncedAngle + 360;
					}
					bullet.angle = Math.toRadians(bouncedAngle);
				}
			}
			
			//there are edge cases where it's possible for a bullet to
			//go outside the game.
			//detect this by assuming that if the bullet isn't
			//inside the game in this or next tick then it has escaped.
			final Point2D.Double nextPos = bullet.getNextPosition();
			if (!isOutsideGame(bullet.x, bullet.y) ||
				!isOutsideGame(nextPos.x, nextPos.y)) {
				returnValue = false;
			}
		}
		
		return returnValue;
	}
	
	private static boolean isOutsideGame(double x, double y)
	{
		return x < 0 || x > 1 ||
			   y < 0 || y > 1;
	}
}























