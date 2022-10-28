package cl.ionix.emulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import cl.ionix.emulator.interfaces.ICipher;
import cl.ionix.emulator.interfaces.ISignature;
import cl.ionix.emulator.utils.MyAESCipher;
import cl.ionix.emulator.utils.MyDESCipher;
import cl.ionix.emulator.utils.MySHAAlgoritms;

@Configuration
public class CipherConfig {

	@Bean("AES")
	@Primary
	public ICipher getAesCipher() {
		return new MyAESCipher();
	}

	@Bean("DES")
	public ICipher getDesCipher() {
		return new MyDESCipher();
	}

	@Bean
	public ISignature getSignatureProccess() {
		return new MySHAAlgoritms();
	}

}
