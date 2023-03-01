package cl.ionix.emulator.config;

import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${app.secure.aes.key:0112f48125034f8fa42aef2441773793}")
	private String aesKey;

	@Bean("AES")
	@Primary
	public ICipher getAesCipher() {
		return new MyAESCipher( aesKey );
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
