package cl.ionix.emulator.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cl.ionix.emulator.interfaces.ICipher;
import java.nio.charset.StandardCharsets;

/**
 * Clase de Cifrado
 * 
 * @author Jonnattan Griffiths
 * @since Programa para TEST
 * @version 1.0 del 16-01-2020
 * 
 */
public class MyDESCipher implements ICipher {

	private final String key;
	private static final String ALG = "DES";
	private DESKeySpec keySpec = null;
	private SecretKeyFactory keyFactory = null;
	private Cipher cipher = null;

	public MyDESCipher() {
		this.key = "jonnattan12345";
		try {
			this.keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
			this.keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey skey = this.keyFactory.generateSecret(this.keySpec);
			this.cipher = Cipher.getInstance(MyDESCipher.ALG);
			this.cipher.init(Cipher.ENCRYPT_MODE, skey);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String encrypt(String aDato) throws Exception {
		String cipherText = aDato;
		if (this.cipher != null) {
			byte[] bytes = aDato.getBytes("UTF-8");
			byte[] result = cipher.doFinal(bytes);
			cipherText = Base64.getEncoder().encodeToString(result);
		} else
			throw new Exception("No se creo cifrador, se va en claro...");
		return cipherText;
	}

	@Override
	public String decrypt(String aDato) throws Exception {
		return null;
	}

}
