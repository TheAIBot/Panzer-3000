package engine;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public final class Crypto {
	
	private static KeyPairGenerator keyGen;
	private static KeyPair pair;
    private static final String ALGORITHM = "DSA";
    private static final String PROVIDER = "SUN";
	
	private Crypto() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {

		// Initialize a generator - DSA is algorithm, SUN is a provider
		keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
		

		// Initialize a random - SHA1PRNG is algorithm, SUN is a provider
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", PROVIDER);
		keyGen.initialize(1024, random);
		
		// Generate keys
		pair = keyGen.generateKeyPair();
		
		
	}
	
	public static KeyPair getPair() {
		return pair;
	}
	
	public static PrivateKey getPrivate() {
		return pair.getPrivate();
	}
	
	public static PublicKey getPublic() {
		return pair.getPublic();
	}
	
	public static byte[] encrypt(byte[] data) throws Exception {
		
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PUBLIC_KEY, pair.getPublic());

		byte[] encryptedBytes = cipher.doFinal(data);

		return encryptedBytes;
	}
	
	public static byte[] decrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PRIVATE_KEY, pair.getPrivate());

		byte[] decryptedBytes = cipher.doFinal(data);

		return decryptedBytes;
		
	} 
	
	
}
