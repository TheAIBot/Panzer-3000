package security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;

import engine.DeSerializer;
import logger.Log;

public class SecureSpaceTools {
	public static boolean encryptAndPut(Space space, PublicKey publickey, Object... fields) throws InterruptedException, Exception {
		return space.put(Crypto.encryptFields(publickey, fields));
	}
	
	public static ArrayList<Object[]> findAllMatchingTuples(Space space, PrivateKey privateKey, boolean removeWhenFound, boolean onlyOne, TemplateField... fields) throws Exception
	{
		final Template template = new Template(fields);		
		final ArrayList<Object[]> matchingTuples = new ArrayList<Object[]>();
		final List<Object[]> tupleList = space.queryAll(new FormalField(byte[].class));
		for (Object[] encryptedObjects : tupleList) {
			final byte[] encryptedObjectBytes = (byte[])encryptedObjects[0];
			byte[] decryptedObjectBytes = null;
			try {
				decryptedObjectBytes = Crypto.decrypt(encryptedObjectBytes, privateKey);
			} catch (Exception e) {
				continue;
			}
			final Object[] decryptedObject = DeSerializer.decodeObjects(decryptedObjectBytes);
			final Tuple tuple = new Tuple(decryptedObject);
			
			if (template.match(tuple)) {
				//if the tuple should be removed then it can only be added to the list of valid tuples
				//when it has been retrieved
				if (removeWhenFound) {
					final Object[] receivedTuple = space.getp(new ActualField(encryptedObjectBytes));
					if (receivedTuple != null) {
						matchingTuples.add(decryptedObject);
					}
				}
				else {
					matchingTuples.add(decryptedObject);
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
