package security;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.TemplateField;

public class SecureSpace {
	private final Space space;
	private final KeyPair encryptionKeys;
	
	public SecureSpace(Space space) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.space = space;
		this.encryptionKeys = Crypto.getPair();
	}

	public boolean put(PublicKey publicKey, Object... fields) throws InterruptedException, Exception {
		return SecureSpaceTools.encryptAndPut(space, publicKey, fields);	
	}
	
	public Object[] get(TemplateField... fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(space, encryptionKeys.getPrivate(), true, true, fields).get(0);
	}
	
	public boolean putWithIdentifier(PublicKey publicKey, Object identifier, Object... fields) throws Exception {
		return SecureSpaceTools.encryptAndPutWithIdentifier(space, publicKey, identifier, fields);
	}
	
	public Object[] getWithIdentifier(TemplateField identifierField) throws Exception {
		return SecureSpaceTools.getAndDecryptWithIdentifier(space, encryptionKeys.getPrivate(), identifierField);
	}
	
	public ArrayList<Object[]> getAll(TemplateField...fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(space, encryptionKeys.getPrivate(), true, false, fields);
	}
	
	public ArrayList<Object[]> getAllWithIdentifier(TemplateField identifier) throws Exception {
		final List<Object[]> result = space.getAll(identifier, new FormalField(byte[].class));
		
		final ArrayList<Object[]> unencrypted = new ArrayList<Object[]>();
		for (Object[] objects : result) {
			final byte[] encryptedBytes = (byte[])objects[1];
			unencrypted.add(Crypto.decryptFields(encryptedBytes, encryptionKeys.getPrivate()));
		}
		
		return unencrypted;
	}
	
	public PublicKey getPublicKey() {
		return encryptionKeys.getPublic();
	}
}
