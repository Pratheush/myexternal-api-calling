package com.mylearning.journalapp.clientconfig;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class MyRestTemplateConfig {

    private final JwtInterceptor jwtInterceptor;

    public MyRestTemplateConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * RestTemplateBuilder is a convenience class that provides a builder-style API for configuring and
     * creating RestTemplate instances with advanced configurations such as timeouts.
     * @param restTemplateBuilder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {

        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .interceptors(jwtInterceptor)
                .build();
    }
}
