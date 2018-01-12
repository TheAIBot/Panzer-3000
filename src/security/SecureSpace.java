package security;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.ArrayList;

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
	
	public ArrayList<Object[]> getAll(TemplateField...fields) throws Exception {
		return SecureSpaceTools.findAllMatchingTuples(space, encryptionKeys.getPrivate(), true, false, fields);
	}
	
	public PublicKey getPublicKey() {
		return encryptionKeys.getPublic();
	}
}
