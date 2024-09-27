package com.mylearning.journalapp.clientconfig;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class MyWebClientConfig {

    @Value("${person.url:http://localhost:8081/api/person}")
    private String personBaseUrl;

    /**
     *  Using WebClient.create()
     *  create() method is an overloaded method and can optionally accept a base URL for requests.
     *
     *  WebClient webClient = WebClient.create();  // With empty URI
     *
     *  WebClient webClient = WebClient.create("https://client-domain.com");  // With specified root URI
     *
     */


    /**
     *
     * Using WebClient.Builder API
     * Configuring Connection Timeouts
     * use Apache HttpClient class to set timeout periods for connection timeout, read timeout and write timeouts.
     *
     * @return
     */
    @Bean
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(10))
                                        .addHandlerLast(new WriteTimeoutHandler(10))));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(personBaseUrl)
                //.clientConnector(connector)
                //.defaultCookie("cookie-name", "cookie-value")
                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
