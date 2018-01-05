package engine;

public class Input {
	public int id;
	public boolean w, a, s, d, click;
	public double x,y;
	
	public Input(boolean w, boolean a, boolean s, boolean d, boolean click, double x, double y, int id) {
		this.w = w;
		this.a = a;
		this.s = s;
		this.d = d;
		this.click = click;
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public Input() {
		
	}
	
}
