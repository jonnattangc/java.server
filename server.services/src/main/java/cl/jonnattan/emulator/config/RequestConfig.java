package cl.jonnattan.emulator.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfig {
	
	@Bean
    public FilterRegistrationBean<AllRequestFilter> RequestFilterBean() {
        FilterRegistrationBean<AllRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AllRequestFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
