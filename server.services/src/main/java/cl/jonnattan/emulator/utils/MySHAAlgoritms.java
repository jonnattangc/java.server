package cl.jonnattan.emulator.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;

import cl.jonnattan.emulator.interfaces.ISignature;

public class MySHAAlgoritms implements ISignature {
	private static final Logger logger = Logger.getLogger(MySHAAlgoritms.class.getName());

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
		byte[] inBytes = input.trim().getBytes(StandardCharsets.UTF_8);
		byte[] outBytes = sha256(inBytes);
		for (byte c : outBytes)
			result += c;
		return result;
	}

	/**
	 * Retorna siempre una cadena de 64 letras. El calculo de SHA-256 son 32 bytes
	 * 40 letras o numeros con una codificación de 256 bits
	 * 
	 * @param input   cualquier texto
	 * @param lowcase true si quieres el resultado en letras munúsculas
	 * @return cadena de 64 letras entre [0-9] y [a-f]
	 */
	public String getHexSHA256(final String input, final boolean lowcase) {
		String result = null;
		byte[] inBytes = input.trim().getBytes(StandardCharsets.UTF_8);
		byte[] outBytes = sha256(inBytes);
		char[] hexa = Hex.encodeHex(outBytes, lowcase);
		result = new String(hexa);
		return result;
	}

	@Override
	public String getSignature(final String data) {
		String signature = getHexSHA256(data, true);
		if (signature != null && signature.equals(data))
			logger.severe("No se pudo calcular firma!!!!");
		return signature;
	}

	public static void main(String[] args) {
		String msg = "Clave_Cifrado_Para_Test";
		MySHAAlgoritms sha = new MySHAAlgoritms();
		System.out.println("Original: " + msg);
		System.out.println("SHA-256 : " + sha.getHexSHA256(msg, false));
		System.out.println("CKSUM   : " + sha.cksum(msg));
	}

}
