package cl.jonnattan.emulator.daos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import cl.jonnattan.emulator.Configuration;

/**
 * Clase DAO de configuraciones
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@Repository
public interface IDaoConfiguration extends CrudRepository<Configuration, Long> {

	public Configuration findByEndpoint(String endpoint);

}
