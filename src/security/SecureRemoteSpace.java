package security;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;
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
	
	public Object[] getEncryptedTuple(ActualField... fields) throws Exception {
		final Object[] objectFields = new Object[fields.length];
		for (int i = 0; i < objectFields.length; i++) {
			objectFields[i] = fields[i].getValue();
		}
		
		final byte[] unencryptedBytes = DeSerializer.encodeObjects(objectFields);
		final byte[] encryptedBytes = Crypto.encrypt(unencryptedBytes, repositoryPublicKey);
		
		return remote.get(new ActualField(encryptedBytes));
	}
	
	//This is not a blocking call.
	//The tuple needs to be in the space already for this to work.
	public Object[] getDecryptableTuple(ActualField... fields) throws Exception {
		return findAllMatchingTuples(true, true, fields).get(0);
	}

	public ArrayList<Object[]> getAllDecryptableTuples(ActualField... fields) throws Exception {
		return findAllMatchingTuples(true, false, fields);
	}

	public Object[] queryDecryptableTuple(ActualField... fields) throws Exception {
		return findAllMatchingTuples(false, true, fields).get(0);
	}

	public List<Object[]> queryAllDecryptableTuples(ActualField... fields) throws Exception {
		return findAllMatchingTuples(false, false, fields);
	}
	
	private ArrayList<Object[]> findAllMatchingTuples(boolean removeWhenFound, boolean onlyOne, ActualField... fields) throws Exception
	{
		final Template template = new Template(fields);		
		final ArrayList<Object[]> matchingTuples = new ArrayList<Object[]>();
		final List<Object[]> tupleList = remote.queryAll(new FormalField(byte[].class));
		for (Object[] encryptedObjects : tupleList) {
			final byte[] encryptedObjectBytes = (byte[])encryptedObjects[0];
			final byte[] decryptedObjectBytes = Crypto.decrypt(encryptedObjectBytes, encryptionKeys.getPrivate());
			final Object[] decryptedObject = DeSerializer.decodeObjects(decryptedObjectBytes);
			final Tuple tuple = new Tuple(decryptedObject);
			
			if (template.match(tuple)) {
				matchingTuples.add(decryptedObject);
				if (removeWhenFound) {
					remote.get(new ActualField(encryptedObjectBytes));
				}
				if (onlyOne) {
					break;
				}
			}
		}
		if (onlyOne && matchingTuples.size() == 0) {
			throw new Exception("Failed to find a matching decryptable tuple");
		}
		
		return matchingTuples;
	}
}
