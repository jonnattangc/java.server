package cl.jonnattan.emulator.daos;

import org.springframework.data.repository.CrudRepository;

import cl.jonnattan.emulator.User;

/**
 * Clase DAO de Usuarios
 * 
 * @author Jonnattan Griffiths
 * @since EMULADOR 
 * @version 1.0 del 22-06-2020
 * 
 */
public interface IDaoUser extends CrudRepository<User, Long> {

	public User findByAccessToken(final String token);
	public User findByNameUserAndPassword(final String nameUser, final String password);
	public User findByRut(final String rut);
	public User findByMail(final String mail);
}
