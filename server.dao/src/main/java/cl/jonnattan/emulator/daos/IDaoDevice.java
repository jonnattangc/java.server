package cl.jonnattan.emulator.daos;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.jonnattan.emulator.Device;

/**
 * Clase DAO de dispositivos
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@Repository
public interface IDaoDevice extends CrudRepository<Device, Long> {
	public Device findByToken(String token);

	public Device findByCard(String card);
	
	public Device findByCryptogram(String cryptogram);

	@Modifying
	@Query("update Device d set d.cryptogram = :cryptogram, d.updatedAt = :date where d.id = :id")
	public void saveCryptogramById(@Param("cryptogram") String cryptogram, @Param("id") Long id,
			@Param("date") Date date);
}
