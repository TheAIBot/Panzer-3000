package security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.TemplateField;

import engine.DeSerializer;

public class SecureRemoteSpace {
	private final RemoteSpace remote;
	private final PublicKey repositoryPublicKey;
	private final KeyPair encryptionKeys;
	
	public SecureRemoteSpace(URI uri, PublicKey repositoryPublicKey) throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		this.remote = new RemoteSpace(uri);
		this.repositoryPublicKey = repositoryPublicKey;
		this.encryptionKeys = Crypto.getPair();
	}

	public int size() throws InterruptedException {
		return remote.queryAll(new FormalField(byte[].class)).size();
	}

	public boolean put(Object... fields) throws Exception {
		return SecureSpaceTools.encryptAndPut(remote, repositoryPublicKey, fields);
	}
	
	public boolean putWithIdentifier(Object identifier, Object... fields) throws Exception {
		return SecureSpaceTools.encryptAndPutWithIdentifier(remote, repositoryPublicKey, identifier, fields);
	}
	
	public Object[] getWithIdentifier(TemplateField identifierField) throws Exception {
		return SecureSpaceTools.getAndDecryptWithIdentifier(remote, encryptionKeys.getPrivate(), identifierField);
	}
	
	public Object[] getEncryptedTuple(ActualField... fields) throws Exception {
		final Object[] objectFields = new Object[fields.length];
		for (int i = 0; i < objectFields.length; i++) {
			objectFields[i] = fields[i].getValue();
		}
		
		final byte[] encryptedBytes = Crypto.encryptFields(repositoryPublicKey, objectFields);		
		return remote.get(new ActualField(encryptedBytes));
	}
	
	//This is not a blocking call.
	//The tuple needs to be in the space already for this to work.
	public Object[] getDecryptableTuple(TemplateField... fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(remote, encryptionKeys.getPrivate(), true, true, fields).get(0);
	}

	public ArrayList<Object[]> getAllDecryptableTuples(TemplateField... fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(remote, encryptionKeys.getPrivate(), true, false, fields);
	}

	public Object[] queryDecryptableTuple(TemplateField... fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(remote, encryptionKeys.getPrivate(), false, true, fields).get(0);
	}

	public ArrayList<Object[]> queryAllDecryptableTuples(TemplateField... fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(remote, encryptionKeys.getPrivate(), false, false, fields);
	}
	
	public void close() throws IOException {
		//remote.close();
	}
}
