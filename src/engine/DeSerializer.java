package engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class DeSerializer {
	public byte[] toBytes() throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				toBytes(out);
				return stream.toByteArray();
			}
		}
	}
	public static <T  extends DeSerializer> byte[] toBytes(ArrayList<T> list) throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				out.writeInt(list.size());
				for (T t : list) {
					t.toBytes(out);
				}
				return stream.toByteArray();
			}
		}
	}
	protected abstract void toBytes(DataOutputStream out) throws IOException;
	
	public void fromBytes(byte[] bytes) throws IOException {
		try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
			try (DataInputStream in = new DataInputStream(stream)) {				
				fromBytes(in);
			}
		}
	}
	public static <T extends DeSerializer> ArrayList<T> toList(byte[] bytes, Class<T> type) throws Exception {
		ArrayList<T> list = new ArrayList<T>();
		try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
			try (DataInputStream in = new DataInputStream(stream)) {
				final int size = in.readInt();
				for (int i = 0; i < size; i++) {
					T t = type.newInstance();
					t.fromBytes(in);
					list.add(t);
				}
			}
		}
		
		return list;		
	}
	protected abstract void fromBytes(DataInputStream in) throws IOException;
}
