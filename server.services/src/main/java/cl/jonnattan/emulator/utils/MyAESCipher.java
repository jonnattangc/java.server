package cl.jonnattan.emulator.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import cl.jonnattan.emulator.interfaces.ICipher;

public class MyAESCipher implements ICipher {

	private static final String AES_CIPHER = "AES/CBC/NoPadding";
	private static final String AES_FACTORY = "PBKDF2WithHmacSHA256";
	private static final String AES_KEY = "AES";
	
	private IvParameterSpec ivspec = null;
	private SecretKeySpec secretKeySpec = null;

	
	public MyAESCipher( String aesKey ) {
		try {
			SecureRandom random = new SecureRandom();
		    byte[] bytesIV = new byte[16];
		    random.nextBytes(bytesIV); // Random initialization vector
			this.ivspec = new IvParameterSpec(bytesIV);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(AES_FACTORY);
			KeySpec spec = new PBEKeySpec(aesKey.toCharArray(), new byte[20], 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			this.secretKeySpec = new SecretKeySpec(tmp.getEncoded(), AES_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	public String encrypt(String aDato) throws Exception {

		Cipher cipher = Cipher.getInstance(AES_CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
		byte[] data = aDato.getBytes(StandardCharsets.UTF_8);
		
		byte[] dataEncrypt = cipher.doFinal( data );
		return Base64.encodeBase64String(dataEncrypt);
	}

	@Override
	public String decrypt(String aDato) throws Exception {
		Cipher cipher = Cipher.getInstance(AES_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
		byte[] data = Base64.decodeBase64(aDato);
		byte[] dataEncrypt = cipher.doFinal( data );
		return new String(dataEncrypt, StandardCharsets.UTF_8);
	}
	
	public static void main(String[] args) throws Exception {
		ICipher cipher = new MyAESCipher("pepitoclavounclavito");
		String dato = "1122334455667788";
		String encryptData = cipher.encrypt(dato);
		String decryptData = cipher.decrypt(encryptData);
		System.err.println("DATO    : " + dato );
		System.err.println("EN CLARO: " + decryptData );
		System.err.println("CIFRADO : " + encryptData );
	}
}
