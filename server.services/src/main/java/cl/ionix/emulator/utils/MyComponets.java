package cl.ionix.emulator.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import cl.ionix.emulator.interfaces.ICipher;

/**
 * Clase de Credenciales
 * 
 * @author Jonnattan Griffiths
 * @since Componentes adicionales
 * @version 1.0 del 16-01-2020
 * 
 */
@Configuration
public class MyComponets
{

  @Bean
  public ICipher getCipher()
  {
    return new MyDESCipher();
  }

}
