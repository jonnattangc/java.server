package cl.jonnattan.emulator.interfaces;

import java.util.Map;

public interface IUtilities {

	/**
	 * Produce un cksum desde la cadena hash que genera la entrada
	 * 
	 * @param data
	 * @return
	 */
	public long cksumSHA256(String data);

	/**
	 * Produce un hash donde la misma información de entrada proporciona siempre la
	 * misma información de salida,
	 * 
	 * @param data
	 * @return
	 */
	public String SHA256(String data);

	/**
	 * Obtiene el token asociado a un tarjeta
	 * @param data
	 * @return
	 */
	public String getTokenCard( String data );
	
	/**
	 * Decifra...
	 * 
	 * @param data
	 * @return
	 */
	public String decryptRSA(String data);
	
	/**
	 * Cifra la data
	 * @param data
	 * @return
	 */
	public String encryptRSA(String data);
	
	/**
	 * Decifra...
	 * 
	 * @param data
	 * @return
	 */
	public String decryptAES(String data);
	
	

	/**
	 * Pasa un Objeto cualquiera a una cadena te texto JSON
	 * 
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj);

	/**
	 * Convierte un JSON a un mapa con sus elementos
	 * 
	 * @param json
	 * @return
	 */
	public Map<String, Object> toMap(String json);
	
}
