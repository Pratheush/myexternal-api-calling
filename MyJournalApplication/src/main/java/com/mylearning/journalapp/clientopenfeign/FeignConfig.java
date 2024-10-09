package com.mylearning.journalapp.clientopenfeign;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 *
 * Decoder – ResponseEntityDecoder, which wraps SpringDecoder, used to decode the Response
 * Encoder – SpringEncoder is used to encode the RequestBody.
 * Logger – Slf4jLogger is the default logger used by Feign.
 * Contract – SpringMvcContract, which provides annotation processing
 * Feign-Builder – HystrixFeign.Builder is used to construct the components.
 * Client – LoadBalancerFeignClient or default Feign client
 *
 */
@EnableFeignClients(basePackages="com.mylearning.journalapp")
@Configuration
@Slf4j
public class FeignConfig {

    private final FeignClientCodeBufferPersonClient feignPersonClient;

    public FeignConfig(@Lazy FeignClientCodeBufferPersonClient feignPersonClient) {
        this.feignPersonClient = feignPersonClient;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        log.info("FeignConfig RequestInterceptor called");
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                // If the request is for the login endpoint, skip adding the Authorization header
                //if(template.url().contains("/login")) return; // Skip the interceptor for login request

                // Check the ThreadLocal context to see if Authorization should be skipped
                if (FeignContextHolder.isSkipAuthorization()) {
                    return; // Skip adding Authorization header
                }

                String jwtAccessToken = feignPersonClient.getJwtAccessToken(); // Implement a method to fetch JWT token
                if (jwtAccessToken != null && !jwtAccessToken.isEmpty()) {
                    log.info("FeignConfig RequestInterceptor if Block jwtAccessToken :: {}",jwtAccessToken);
                    template.header("Authorization", jwtAccessToken);
                }
            }
        };
    }

    // USING LAMBDA
    /*@Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            String token = feignPersonClient.getJwtAccessToken(); // Implement a method to fetch JWT token
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }*/

    @Bean
    public Logger.Level feignLoggerLevel() {
        log.info("FeignConfig feignLoggerLevel called");
        return Logger.Level.FULL; // Enable FULL logging for detailed request information
    }

}
