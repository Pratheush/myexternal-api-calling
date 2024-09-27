package com.mylearning.journalapp.clientopenfeign;

import com.mylearning.journalapp.clientexception.PersonCallingClientException;
import com.mylearning.journalapp.clientexception.PersonCallingServerException;
import com.mylearning.journalapp.clientexception.PersonNotFoundException;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.context.annotation.Bean;

import java.util.Base64;

/**
 *
 * Notes:
 * Gson Encoder/Decoder: Used for encoding and decoding JSON requests and responses.
 * JWT Authentication: Uses a custom interceptor to add the JWT token to the Authorization header.
 * Basic Auth: Another interceptor handles Basic Authentication headers.
 * OAuth2 Support: Optionally, you can use OAuth2 for token-based authentication.
 * Timeouts and Retries: Configured via Options and Retryer.
 * Error Handling: Uses ErrorDecoder to handle HTTP errors like 400, 404, and 500.
 * Interceptors: Multiple interceptors are provided for logging, authentication, and retry handling.
 *
 *
 *       four logging levels to choose
 *       NONE – no logging, which is the default
 *       BASIC – log only the request method, URL and response status
 *       HEADERS – log the basic information together with request and response headers
 *       FULL – log the body, headers and metadata for both request and response
 *
 *       below bean not configured for logger for FeignClient
 *       @return
 *
 *
 */
//@Configuration
public class MyFeignClientConfiguration {
    // Logger Configuration
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log full request and response details
    }

    // Custom Gson Encoder
    @Bean
    public Encoder feignEncoder() {
        return new GsonEncoder();
    }

    // Custom Gson Decoder
    @Bean
    public Decoder feignDecoder() {
        return new GsonDecoder();
    }

    // JWT Interceptor
    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            String token = getJwtToken(); // Implement a method to fetch JWT token
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

    // Basic Auth Interceptor
    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return requestTemplate -> {
            String username = "yourUsername";
            String password = "yourPassword";
            String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            requestTemplate.header("Authorization", "Basic " + basicAuth);
        };
    }

    // OAuth2 Interceptor (Optional if using OAuth2)
    @Bean
    public RequestInterceptor oauth2RequestInterceptor() {
        return requestTemplate -> {
            String accessToken = getOAuth2Token(); // Implement method to fetch OAuth2 token
            requestTemplate.header("Authorization", "Bearer " + accessToken);
        };
    }

    // Feign Retry Handling
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 5); // 1000ms interval, 2000ms max, 5 retry attempts
    }

    // Connection and Read Timeouts
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 30000); // 5s connection timeout, 30s read timeout
    }

    // Custom Feign Error Decoder
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            switch (response.status()) {
                case 400:
                    return new PersonCallingClientException("Bad Request");
                case 404:
                    return new PersonNotFoundException("Not Found");
                case 500:
                    return new PersonCallingServerException("Internal Server Error");
                default:
                    return new Exception("Generic Error");
            }
        };
    }

    // Feign Request Interceptors
    @Bean
    public RequestInterceptor loggingInterceptor() {
        return requestTemplate -> {
            // Log request details here
            System.out.println("Feign Request: " + requestTemplate.url());
        };
    }

    // Configure BasicAuth if needed
    @Bean
    public BasicAuthRequestInterceptor basicAuthInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }

    // OAuth2 Request Interceptor (if you want to handle OAuth2)
    /*@Bean
    public RequestInterceptor oAuth2FeignRequestInterceptor(OAuth2AuthorizedClientService clientService) {
        return new OAuth2FeignRequestInterceptor(clientService);
    }*/

    // Implement a method to retrieve the JWT token
    private String getJwtToken() {
        // Your logic to retrieve the JWT token (from a service, or security context)
        return "your-jwt-token";
    }

    // Implement OAuth2 token retrieval (optional)
    private String getOAuth2Token() {
        // Your logic to retrieve the OAuth2 token
        return "your-oauth2-token";
    }
}
