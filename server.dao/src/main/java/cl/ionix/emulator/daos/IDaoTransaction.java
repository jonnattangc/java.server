package cl.ionix.emulator.daos;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cl.ionix.emulator.Transaction;
import cl.ionix.emulator.enums.TransactionStatus;
import org.springframework.data.repository.query.Param;

/**
 * Clase DAOde transacciones
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
public interface IDaoTransaction extends CrudRepository<Transaction, Long> {

	public Transaction findByAuthorizationId(String authorizationId);

	@Modifying
	@Query("update Transaction u set u.status = :status, u.updatedAt = :date where u.id = :id")
	public void saveStatusById(@Param("status") TransactionStatus status, @Param("id") Long id,
			@Param("date") Date date);

}
