package graphics;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;

public class Renderer {
		
	private final JFrame frame;
		
	//renderTank(double x, double y, double bodyWidth, double bodyHeight, double bodyAngle, double gunAngle);
	//renderBullet(double x, double y, double width, double height);
	//renderObstacle();
	
	public Renderer()
	{
		frame = new JFrame();
		frame.setSize(new Dimension(500, 500));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.show();
	}

}
