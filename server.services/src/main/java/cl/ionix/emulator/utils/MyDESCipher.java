package cl.ionix.emulator.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cl.ionix.emulator.interfaces.ICipher;

/**
 * Clase de Cifrado
 * 
 * @author Jonnattan Griffiths
 * @since Programa para TEST
 * @version 1.0 del 16-01-2020
 * 
 */
public class MyDESCipher implements ICipher {

	private final static String key = "jonnattan12345";
	private DESKeySpec keySpec = null;
	private SecretKeyFactory keyFactory = null;
	private Cipher cipher = null;

	public MyDESCipher() {
		initialize();
	}

	private void initialize() {
		try {
			this.keySpec = new DESKeySpec(key.getBytes("UTF-8"));
			this.keyFactory = SecretKeyFactory.getInstance("DES");
			if (this.keySpec != null && this.keyFactory != null) {
				{
					SecretKey skey = this.keyFactory.generateSecret(this.keySpec);
					this.cipher = Cipher.getInstance("DES");
					this.cipher.init(Cipher.ENCRYPT_MODE, skey);
				}
			}
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
			new Exception("No se creo cifrador, se va en claro...");
		return cipherText;
	}

	@Override
	public String decrypt(String aDato) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
