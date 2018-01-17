package security;

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

import engine.DeSerializer;


public final class Crypto {
	
    private static final String ALGORITHM = "RSA";
    private static final String CYPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String PROVIDER = "SunRsaSign";
    private static final String RAND_PROVIDER = "SUN";
    private static final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int ENCRYPTION_BIT_LENGTH = 1024;
    private static final int ENCRYPTED_DATA_LENGTH = ENCRYPTION_BIT_LENGTH / 8;
    private static final int ENCRYPTION_PADDING = 11;
    private static final int MAX_ENCRYPTABLE_DATA_SIZE = ENCRYPTED_DATA_LENGTH - ENCRYPTION_PADDING;
	
	private Crypto()  {
		// Empty
	}
	
	public static KeyPair getPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		// Initialize a generator - RSA is algorithm, SUN is a provider
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
		
		// Initialize a random - SHA1PRNG is algorithm, SUN is a provider
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", RAND_PROVIDER);
		keyGen.initialize(ENCRYPTION_BIT_LENGTH, random);
		
		// Generate keys
		return keyGen.generateKeyPair();
	}
	
	public static byte[][] encrypt(byte[][] data, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(CYPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		final byte[][] encryptedData = new byte[data.length][];
		for (int i = 0; i < data.length; i++) {
			encryptedData[i] = cipher.doFinal(data[i]);
		}
		return encryptedData;
	}
	
	public static byte[][] decrypt(byte[][] data, PrivateKey privateKey) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(CYPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		final byte[][] decryptedData = new byte[data.length][];
		for (int i = 0; i < data.length; i++) {
			decryptedData[i] = cipher.doFinal(data[i]);
		}
		
		return decryptedData;
	}
	
	public static String getRandomString(int length) throws NoSuchAlgorithmException, NoSuchProviderException {
        final SecureRandom random = SecureRandom.getInstance("SHA1PRNG", RAND_PROVIDER);
        final StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
        	final int index = random.nextInt(SALTCHARS.length());
        	randomString.append(SALTCHARS.charAt(index));
		}
        
        return randomString.toString();
    }
	
	public static PublicKey unencode(byte[] publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance(ALGORITHM, PROVIDER).generatePublic(new X509EncodedKeySpec(publicKey));
	}
	
	public static byte[] encryptFields(PublicKey publicKey, Object... fields) throws Exception {
		final byte[] fieldsBytes = DeSerializer.encodeObjects(fields);
		final byte[][] fieldsBytesParts = splitIntoArrayOfMaxSize(fieldsBytes, MAX_ENCRYPTABLE_DATA_SIZE);
		final byte[][] encryptedParts = encrypt(fieldsBytesParts, publicKey);
		return combineByteArrays(encryptedParts);
	}
	
	public static Object[] decryptFields(byte[] fieldsInBytes, PrivateKey privateKey) throws Exception {
		final byte[][] encryptedParts = splitIntoArrayOfMaxSize(fieldsInBytes, ENCRYPTED_DATA_LENGTH);
		final byte[][] decryptedBytesParts = decrypt(encryptedParts, privateKey);
		final byte[] fieldsBytes = combineByteArrays(decryptedBytesParts);
		return DeSerializer.decodeObjects(fieldsBytes);
	}
	
	private static byte[][] splitIntoArrayOfMaxSize(byte[] toEncrypt, int maxSize) {
		final int arraysNeeded = Math.max(1, toEncrypt.length / maxSize);
		final byte[][] toEncryptParts = new byte[arraysNeeded][];
		
		int bytesLeft = toEncrypt.length;
		for (int i = 0; i < toEncryptParts.length; i++) {
			toEncryptParts[i] = new byte[Math.min(bytesLeft, maxSize)];
			System.arraycopy(toEncrypt, i * maxSize, toEncryptParts[i], 0, toEncryptParts[i].length);
		}
		
		return toEncryptParts;
	}
	
	private static byte[] combineByteArrays(byte[][] toCombine) {
		int combinedSize = 0;
		for (int i = 0; i < toCombine.length; i++) {
			combinedSize += toCombine[i].length;
		}
		
		final byte[] combinedData = new byte[combinedSize];
		int bytesCombined = 0;
		for (int i = 0; i < toCombine.length; i++) {
			System.arraycopy(toCombine[i], 0, combinedData, bytesCombined, toCombine[i].length);
			bytesCombined += toCombine[i].length;
		}
		
		return combinedData;
	}
	
	
}
