package cl.ionix.emulator.daos;

import org.springframework.data.repository.CrudRepository;

import cl.ionix.emulator.User;

/**
 * Clase DAO de Usuarios
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR APITEC
 * @version 1.0 del 22-06-2020
 * 
 */
public interface IDaoUser extends CrudRepository<User, Long> {

	public User findByAccessToken(String token);
	public User findByNameUserAndPassword(String nameUser, String password);

}
