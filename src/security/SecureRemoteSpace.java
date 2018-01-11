package security;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;

import engine.DeSerializer;
import logger.Log;

public class SecureRemoteSpace {
	private final RemoteSpace remote;
	private final PublicKey repositoryPublicKey;
	private final KeyPair encryptionKeys;
	
	public SecureRemoteSpace(String uri, PublicKey repositoryPublicKey) throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		this.remote = new RemoteSpace(uri);
		this.repositoryPublicKey = repositoryPublicKey;
		this.encryptionKeys = Crypto.getPair();
	}

	public int size() {
		return remote.size();
	}

	public boolean put(Object... fields) throws Exception {
		return remote.put(Crypto.encryptFields(encryptionKeys.getPublic(), fields));
	}

	public Object[] get(TemplateField... fields) throws Exception {
		final Template template = new Template(fields);
		final List<Object[]> tupleList = remote.queryAll(new FormalField(byte[].class));
		for (Object[] encryptedObjects : tupleList) {
			final byte[] encryptedObjectBytes = (byte[])encryptedObjects[0];
			Object[] decryptedObject = DeSerializer.decodeObjects(encryptedObjectBytes);
			final Tuple tuple = new Tuple(decryptedObject);
			if (template.match(tuple)) {
				remote.get(new ActualField(encryptedObjectBytes));
				return decryptedObject;
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getp(TemplateField... fields) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object[]> getAll(TemplateField... fields) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] query(TemplateField... fields) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] queryp(TemplateField... fields) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object[]> queryAll(TemplateField... fields) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
