package cl.jonnattan.emulator.interfaces;

/**
 * Interfaz para cifrar/decifrar
 * 
 * @author Jonnattan Griffiths
 * @since Programa para TEST IONIX
 * @version 1.0 del 16-01-2020
 * 
 */
public interface ICipher {

	public String encrypt(final String aDato) throws Exception;
	
	public String decrypt(final String aDato) throws Exception;

}
