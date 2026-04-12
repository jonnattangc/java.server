package cl.jonnattan.emulator.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Deshabilitamos CSRF para pruebas de API
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // Permitimos todas las peticiones sin autenticación

        return http.build();
    }
}