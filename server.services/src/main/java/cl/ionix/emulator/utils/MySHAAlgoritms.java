package cl.ionix.emulator.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;

import cl.ionix.emulator.interfaces.ISignature;

public class MySHAAlgoritms implements ISignature {
	private final static Logger logger = Logger.getLogger(MySHAAlgoritms.class.getName());

	private MessageDigest digest = null;

	public MySHAAlgoritms() {
		initialize();
	}

	/**
	 * Inicia el digest
	 */
	private void initialize() {
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			digest = null;
		}
	}

	/**
	 * Obtiene el hash-256 de una cadena de caracteres
	 * 
	 * @param input
	 * @return HASH-256
	 */
	private byte[] sha256(final byte[] input) {
		return digest.digest(input);
	}

	@Override
	public long cksum(String input) {
		long result = 0L;
		try {
			byte[] inBytes = input.trim().getBytes("UTF-8");
			byte[] outBytes = sha256( inBytes );
			for (byte c : outBytes)
				result += c;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Retorna siempre una cadena de 64 letras. El calculo de SHA-256 son 32 bytes
	 * 40 letras o numeros con una codificación de 256 bits
	 * @param input cualquier texto
	 * @param lowcase true si quieres el resultado en letras munúsculas
	 * @return cadena de 64 letras entre [0-9] y [a-f]
	 */
	public String getHexSHA256(final String input, final boolean lowcase ) {
		String result = input;
		try {
			byte[] inBytes = input.trim().getBytes("UTF-8");
			byte[] outBytes = sha256( inBytes );
			char[] hexa = Hex.encodeHex(outBytes, lowcase );
			result = new String(hexa);
		} catch (UnsupportedEncodingException e) {
			logger.severe("no se pude tener la codificación UTF-8: " + e.getMessage() );
			result = input;
		}
		return result;
	}

	@Override
	public String getSignature(final String data) {
		String signature = getHexSHA256( data, true );
		if( signature != null && signature.equals(data))
			logger.severe("No se pudo calcular firma!!!!");
		return signature;
	}
	
	public static void main(String[] args) {
		String msg = "Clave_Cifrado_Ionix_Edg";
		MySHAAlgoritms sha = new MySHAAlgoritms();
		System.out.println("Original: " + msg );
		System.out.println("SHA-256 : " + sha.getHexSHA256(msg, false) );
		System.out.println("CKSUM   : " + sha.cksum(msg) );
	}

}
