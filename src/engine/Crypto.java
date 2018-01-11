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
    private static final String PROVIDER = "SUN";
    private static final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	
	private Crypto()  {
		// Empty
	}
	
	public static KeyPair getPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		// Initialize a generator - DSA is algorithm, SUN is a provider
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		
		// Initialize a random - SHA1PRNG is algorithm, SUN is a provider
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", PROVIDER);
		keyGen.initialize(1024, random);
		
		// Generate keys
		return keyGen.generateKeyPair();
	}
	
	public static String encrypt(String data, PublicKey publicKey) throws Exception {
		
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPublicKeySpec keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		
		byte[] byteData = data.getBytes();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PUBLIC_KEY, publicKey);

		byte[] encryptedBytes = cipher.doFinal(byteData);

		return new String(encryptedBytes);
	}
	
	public static String decrypt(String data, PrivateKey privateKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {

		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPrivateKeySpec keySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		
		byte[] byteData = data.getBytes();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PRIVATE_KEY, privateKey);

		byte[] decryptedBytes = cipher.doFinal(byteData);

		return new String(decryptedBytes);
		
	} 
	
	public static String getSaltString() {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	
	public static PublicKey unencode(byte[] publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
	}
	
}
