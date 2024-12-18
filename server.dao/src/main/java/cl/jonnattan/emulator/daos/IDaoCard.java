package cl.jonnattan.emulator.daos;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cl.jonnattan.emulator.interfaces.ICard;

/**
 * Clase DAO de tarjetas
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
public interface IDaoCard extends CrudRepository<ICard, Long> {

	public ICard findByToken(String token);

	@Query("select c from Card c where c.client = :client")
	public List<ICard> findByClient(@Param("client") String client);

	public ICard findByCardNumber(String cardNumber);

	@Modifying
	@Query("update Card c set c.amount = :amount, c.updatedAt = :date where c.id = :id")
	public void saveAmountById(@Param("amount") Long amount, @Param("id") Long id, @Param("date") Date date);
}
