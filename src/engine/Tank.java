package engine;

public class Tank {
	public double x;
	public double y;
	public double bodyWidth;
	public double bodyHeight;
	public double bodyAngle;
	public double gunAngle;
	public int id;
	
	public Tank(double xNew, double yNew, double bodyWidthNew, double bodyHeightNew, 
			double bodyAngleNew, double gunAngleNew, int idNew) {
		
		x = xNew;
		y = yNew;
		bodyWidth = bodyWidthNew;
		bodyHeight = bodyHeightNew;
		bodyAngle = bodyAngleNew;
		gunAngle = gunAngleNew;
		id = idNew;
	}
	
}
