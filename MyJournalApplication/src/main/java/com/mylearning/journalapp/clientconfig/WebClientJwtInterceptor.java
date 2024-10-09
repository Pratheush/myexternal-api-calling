package com.mylearning.journalapp.clientconfig;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * this interceptor does not work with webclient
 *
 *  Modify the JwtInterceptor to an ExchangeFilterFunction
 *  To add the custom JwtInterceptor to the WebClient configuration, you need to use a custom ExchangeFilterFunction.
 *  ExchangeFilterFunction is the way to add interceptors or request manipulation in WebClient since it doesn't
 *  directly support ClientHttpRequestInterceptor like RestTemplate.
 *
 */
@Component
@Slf4j
public class WebClientJwtInterceptor implements ExchangeFilterFunction {

    private final MyPersonClientInterface myPersonClientInterface;

    // Spring will automatically inject the active implementation of MyPersonClientInterface
    public WebClientJwtInterceptor(@Lazy MyPersonClientInterface myPersonClientInterface) {
        this.myPersonClientInterface = myPersonClientInterface;
    }
    @Override
    public @NonNull Mono<ClientResponse> filter(@NonNull ClientRequest request, ExchangeFunction next) {

        // If the request is for the login endpoint, skip adding the Authorization header
        // Check if the request is a login request through /login in url
        //if(request.url().toString().contains("/login")) return next.exchange(request); // Skip the JWT interceptor for login

        // Check the ThreadLocal context to see if Authorization should be skipped
        if (InterceptorContext.isInterceptorDisabled()) {
            return next.exchange(request); // Skip if disabled, Skip adding Authorization header
        }

        String jwtAccessToken = myPersonClientInterface.getJwtAccessToken();

        // Print out the JWT token to verify its content
        log.info("JWT Access Token in Interceptor: {}", jwtAccessToken);

        // Ensure the token is not empty or malformed
        if (jwtAccessToken == null || jwtAccessToken.trim().isEmpty() || !jwtAccessToken.contains(".")) {
            log.error("Invalid JWT Token: {}", jwtAccessToken);
            throw new IllegalArgumentException("JWT Access Token is missing or invalid");
        }

        // Modify the request headers to include the JWT token
        ClientRequest modifiedRequest = ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, jwtAccessToken)
                .build();

        return next.exchange(modifiedRequest);
    }
}
