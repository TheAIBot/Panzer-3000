package engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Input extends DeSerializer {
	public int id = 0;
	public boolean w = false;
	public boolean a = false;
	public boolean s = false;
	public boolean d = false;
	public boolean click = false;
	public double x = 0;
	public double y = 0;
	
	public Input(boolean w, boolean a, boolean s, boolean d, boolean click, double x, double y) {
		this.w = w;
		this.a = a;
		this.s = s;
		this.d = d;
		this.click = click;
		this.x = x;
		this.y = y;
	}
	
	public Input() {
		
	}
	
	@Override
	public void toBytes(DataOutputStream out) throws IOException {
		out.writeInt(id);
		
		final int wByte     = w     ? 0b00000001 : 0;
		final int aByte     = a     ? 0b00000010 : 0;
		final int sByte     = s     ? 0b00000100 : 0;
		final int dByte     = d     ? 0b00001000 : 0;
		final int clickByte = click ? 0b00010000 : 0;
		final int booleanInfo = wByte | aByte | sByte | dByte | clickByte;
		
		out.writeByte(booleanInfo);
		out.writeFloat((float) x);
		out.writeFloat((float) y);
	}

	@Override
	public void fromBytes(DataInputStream in) throws IOException {
		id = in.readInt();
		
		final int booleanInfo = in.readByte();
		w     = (booleanInfo & 0b00000001) > 0;
		a     = (booleanInfo & 0b00000010) > 0;
		s     = (booleanInfo & 0b00000100) > 0;
		d     = (booleanInfo & 0b00001000) > 0;
		click = (booleanInfo & 0b00010000) > 0;
		
		x = in.readFloat();
		y = in.readFloat();
	}
	
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append(w ? "w" : " ");
		b.append(a ? "a" : " ");
		b.append(s ? "s" : " ");
		b.append(d ? "d" : " ");
		b.append(click? "click": "     ");
		b.append(x);
		b.append(" ");
		b.append(y);
		
		return b.toString();
	}
	
	public Input copy() {
		return new Input(w, a, s, d, click, x, y);
	}
}
