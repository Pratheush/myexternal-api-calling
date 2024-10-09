package com.mylearning.journalapp.clientconfig;

import com.mylearning.journalapp.client.RestClientCodeBufferPersonClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class MyRestClientConfig {

    @Value("${person.url:http://localhost:8081/api/person}")
    private String baseURI;

    private final JwtInterceptor jwtInterceptor;

    public MyRestClientConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create(baseURI);
    }

    @Bean
    public PersonClient personClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseURI)
                .requestInterceptors(clientHttpRequestInterceptors -> clientHttpRequestInterceptors.add(jwtInterceptor))
                .requestFactory(getClientRequestFactory())
                .build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(PersonClient.class);
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        ClientHttpRequestFactorySettings clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(20))
                .withReadTimeout(Duration.ofSeconds(20));
        return ClientHttpRequestFactories.get(clientHttpRequestFactorySettings);
    }
}
