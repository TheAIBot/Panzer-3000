package engine;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public final class Crypto {
	
    private static final String ALGORITHM = "RSA";
    private static final String CYPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String PROVIDER = "SunRsaSign";
    private static final String RAND_PROVIDER = "SUN";
    private static final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	private Crypto()  {
		// Empty
	}
	
	public static KeyPair getPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		// Initialize a generator - DSA is algorithm, SUN is a provider
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
		
		// Initialize a random - SHA1PRNG is algorithm, SUN is a provider
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", RAND_PROVIDER);
		keyGen.initialize(1024, random);
		
		// Generate keys
		return keyGen.generateKeyPair();
	}
	
	public static byte[] encrypt(String data, PublicKey publicKey) throws Exception {
		
		Cipher cipher = Cipher.getInstance(CYPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] encryptedBytes = cipher.doFinal(data.getBytes());

		return encryptedBytes;
	}
	
	public static String decrypt(byte[] data, PrivateKey privateKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
		
		Cipher cipher = Cipher.getInstance(CYPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] decryptedBytes = cipher.doFinal(data);

		return new String(decryptedBytes);
		
	} 
	
	public static String getSaltString(int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) rnd.nextInt(SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	
	public static PublicKey unencode(byte[] publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance(ALGORITHM, PROVIDER).generatePublic(new X509EncodedKeySpec(publicKey));
	}
	
}
