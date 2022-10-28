package cl.ionix.emulator.config;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.function.Supplier;
import java.util.logging.Logger;

@Configuration
public class HttpClientConfig {

    private static final int SECOND = 1000;

    private final static Logger logger = Logger.getLogger(HttpClientConfig.class.getName());

    
    @Value("${http.timeout.connect:10}")
    private int connectTimeout;

    @Value("${http.timeout.read:10}")
    private int readTimeout;

    @Bean
    @RequestScope
    RestTemplate restTemplateNoEncoding() {
        return requestNoEncoding();
    }

    @Bean
    @RequestScope
    RestTemplate restTemplateWithTimeout() {
        return requestWithTimeout();
    }

    @Bean
    @RequestScope
    RestTemplate restTemplate() {
        return request();
    }

    @Bean
    RestTemplate restTemplateNoEncodingTest() {
        return requestNoEncoding();
    }

    @Bean
    @Primary
    RestTemplate restTemplateWithTimeoutTest() {
        return requestWithTimeout();
    }

    @Bean
    RestTemplate restTemplateTest() {
        return request();
    }

    private RestTemplate requestNoEncoding() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        return restTemplate;
    }

    private RestTemplate requestWithTimeout() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.requestFactory(new RequestFactorySupplier()).build();
    }

    private RestTemplate request() {
        return new RestTemplate();
    }

    private static class CustomHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

        CustomHttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
            super(httpClient);
        }

        @Override
        protected void postProcessHttpRequest(HttpUriRequest request) {
            HttpRequestBase req = (HttpRequestBase) request;
            req.setProtocolVersion(HttpVersion.HTTP_1_0);
            super.postProcessHttpRequest(request);
        }
    }

    private class RequestFactorySupplier implements Supplier<ClientHttpRequestFactory> {

        @Override
        public ClientHttpRequestFactory get() {

            // Using Apache HTTP client.
            HttpClientBuilder clientBuilder = HttpClientBuilder.create();

            HttpComponentsClientHttpRequestFactory requestFactory = new CustomHttpComponentsClientHttpRequestFactory(clientBuilder.build());
            logger.info(String.format("connectTimeout[%d] readTimeout[%d]", connectTimeout, readTimeout ));
            // When sending large amounts of data via POST or PUT, it is recommended to change this property to false, so as not to run out of memory.
            requestFactory.setBufferRequestBody(false);
            requestFactory.setConnectionRequestTimeout(connectTimeout * SECOND);
            requestFactory.setReadTimeout(readTimeout * SECOND);

            return requestFactory;
        }
    }
}
