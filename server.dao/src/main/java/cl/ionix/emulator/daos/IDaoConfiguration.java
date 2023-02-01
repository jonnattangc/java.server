package cl.ionix.emulator.daos;

import org.springframework.data.repository.CrudRepository;

import cl.ionix.emulator.Configuration;

/**
 * Clase DAO de configuraciones
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
public interface IDaoConfiguration extends CrudRepository<Configuration, Long> {

	public Configuration findByEndpoint(String endpoint);

}
