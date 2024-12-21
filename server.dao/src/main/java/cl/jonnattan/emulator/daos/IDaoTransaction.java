package cl.jonnattan.emulator.daos;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.jonnattan.emulator.Transaction;
import cl.jonnattan.emulator.enums.TransactionStatus;

/**
 * Clase DAOde transacciones
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
@Repository
public interface IDaoTransaction extends CrudRepository<Transaction, Long> {

	public Transaction findByAuthorizationId(String authorizationId);

	@Modifying
	@Query("update Transaction u set u.status = :status, u.updatedAt = :date where u.id = :id")
	public void saveStatusById(@Param("status") TransactionStatus status, @Param("id") Long id,
			@Param("date") Date date);

}
