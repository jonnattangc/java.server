package cl.jonnattan.emulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase de Credenciales
 *
 * @author Jonnattan Griffiths
 * @since Programa para emular servicios utiles de test
 * @version 1.0 del 16-01-2020
 *
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "cl.jonnattan.emulator.daos")
@EntityScan(basePackages = "cl.jonnattan.emulator")
public class Application {

	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}

}