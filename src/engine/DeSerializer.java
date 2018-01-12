package engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

import com.sun.corba.se.spi.legacy.interceptor.UnknownType;

import network.spaces.ClientInfo;
import security.Crypto;

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
	public abstract void toBytes(DataOutputStream out) throws IOException;
	
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
	public abstract void fromBytes(DataInputStream in) throws IOException;
	
	public static byte[] encodeObjects(Object... objects) throws Exception {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			try (DataOutputStream out = new DataOutputStream(stream)) {
				//decoder needs to know how many objects to create
				out.writeInt(objects.length);
				for (int i = 0; i < objects.length; i++) {
					objectToBytes(objects[i], out);
				}
				
				return stream.toByteArray();
			}
		}
	}
	
	public static Object[] decodeObjects(byte[] encoded) throws Exception {
		try (ByteArrayInputStream stream = new ByteArrayInputStream(encoded)) {
			try (DataInputStream in = new DataInputStream(stream)) {
				final int objectCount = in.readInt();
				final Object[] objects = new Object[objectCount];
				for (int i = 0; i < objects.length; i++) {
					objects[i] = bytesToObject(in);
				}
				
				return objects;
			}
		}
	}
	
	private static void objectToBytes(Object object, DataOutputStream out) throws Exception {
		//for each if there is at the top a writeByte
		//call. This is used to tell the decoder which
		//type was encoded here, so the decoder can decode it.
		//That is then followed by the bytes of the decoded data.
		if (object instanceof byte[]) {
			out.writeByte(0);
			
			final byte[] data = (byte[])object;
			out.writeInt(data.length);
			out.write(data);
		}
		else if (object instanceof String) {
			out.writeByte(1);
			
			final String data = (String)object;
			out.writeUTF(data);
		}
		else if (object instanceof Integer) {
			out.writeByte(2);
			
			final int data = (int)object;
			out.writeInt(data);
		}
		else if (object instanceof PublicKey) {
			out.writeByte(3);
			
			final PublicKey data = (PublicKey)object;
			final byte[] dataBytes = data.getEncoded();
			out.writeInt(dataBytes.length);
			out.write(dataBytes);
		}
		else if (object instanceof ClientInfo) {
			out.writeByte(4);
			
			final ClientInfo data = (ClientInfo)object;
			data.toBytes(out);
		}
		else {
			throw new UnknownType("Type is not supported to be encoded. Type: " + object.getClass().getName());
		}
	}
	
	private static Object bytesToObject(DataInputStream in) throws Exception {
		final int type = in.readByte();
		
		if (type == 0) {
			final int length = in.readInt();
			final byte[] data = new byte[length];
			in.readFully(data, 0, length);
			return data;
		}
		else if (type == 1) {
			return in.readUTF();
		}
		else if (type == 2) {
			return in.readInt();
		}
		else if (type == 3) {
			final int length = in.readInt();
			
			final byte[] data = new byte[length];
			in.readFully(data, 0, length);
			return Crypto.unencode(data);
		}
		else if (type == 4) {
			final ClientInfo data = new ClientInfo();
			data.fromBytes(in);
			return data;
		}
		else {
			throw new UnknownType("Type is not supported to be decoded");
		}
	}
}
