package engine;

public class Tank {
	double x;
	double y;
	double bodyWidth;
	double bodyHeight;
	double bodyAngle;
	double gunAngle;
	int id;
	
	Tank(double xNew, double yNew, double bodyWidthNew, double bodyHeightNew, 
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
