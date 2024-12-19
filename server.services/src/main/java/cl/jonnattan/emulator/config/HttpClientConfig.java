package cl.jonnattan.emulator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class HttpClientConfig {

	@Value("${http.timeout.connect:10}")
	private int connectTimeout;

	@Value("${http.timeout.read:10}")
	private int readTimeout;

	@Bean
	@RequestScope
	@Primary
	RestTemplate restTemplate() {
		return request();
	}

	@Bean
	RestTemplate restTemplateTest() {
		return request();
	}

	private RestTemplate request() {
		return new RestTemplate();
	}

}
