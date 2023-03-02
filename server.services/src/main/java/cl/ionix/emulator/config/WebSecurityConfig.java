package cl.ionix.emulator.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		    .and().authorizeRequests()
				.antMatchers("/config/**","/page/users/save").authenticated();
	}
	
	@Override
 	public void configure(WebSecurity web) throws Exception {
 		web.ignoring().antMatchers("/page/users","/login");
 	}
	
	@Override
 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER")
 				.and().withUser("admin").password("password").roles("ADMIN", "USER");
 	}

	@Bean
	@Profile("dev")
	public RequestCache refererRequestCache() {
		return new HttpSessionRequestCache() {
			@Override
			public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
				String referrer = request.getHeader("referer");
				if (referrer != null) {
					request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST",
							new SimpleSavedRequest(referrer));
				}
			}
		};
	}
	
	@Bean 
	public PasswordEncoder passwordEncoder() { 
	    return new BCryptPasswordEncoder(); 
	}
	
	@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
            .password(passwordEncoder().encode("user1Pass"))
            .roles("USER")
            .build();
        UserDetails user2 = User.withUsername("user2")
            .password(passwordEncoder().encode("user2Pass"))
            .roles("USER")
            .build();
        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }
}
