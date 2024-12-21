package cl.jonnattan.emulator.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.jonnattan.emulator.interfaces.ICipher;

/**
 * Clase de Credenciales
 * 
 * @author Jonnattan Griffiths
 * @since Componentes adicionales
 * @version 1.0 del 16-01-2020
 * 
 */
@Configuration
public class MyComponets {

  @Bean
  ICipher getCipher() {
    return new MyDESCipher();
  }

}
