package com.mylearning.journalapp.clientconfig;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Using the Conditional Bean allows us to use only one implementation will be active at a time,
 * so we don’t need to use @Qualifier or handle multiple beans.
 *
 * Modify the JwtInterceptor to an ExchangeFilterFunction
 * To add the custom JwtInterceptor to the WebClient configuration, you need to use a custom ExchangeFilterFunction.
 * ExchangeFilterFunction is the way to add interceptors or request manipulation in WebClient since it doesn't
 * directly support ClientHttpRequestInterceptor like RestTemplate.
 *
 *
 */
@Component
@Slf4j
public class JwtInterceptor implements ClientHttpRequestInterceptor {

    private final MyPersonClientInterface myPersonClientInterface;

    // Spring will automatically inject the active implementation of MyPersonClientInterface
    public JwtInterceptor(@Lazy MyPersonClientInterface myPersonClientInterface) {
        this.myPersonClientInterface = myPersonClientInterface;
    }

    @Override
    public @NonNull ClientHttpResponse intercept(HttpRequest request, byte @NonNull [] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders requestHeaders = request.getHeaders();
        String jwtAccessToken = myPersonClientInterface.getJwtAccessToken();
        requestHeaders.add("Authorization", jwtAccessToken);
        return execution.execute(request,body);
    }


}
