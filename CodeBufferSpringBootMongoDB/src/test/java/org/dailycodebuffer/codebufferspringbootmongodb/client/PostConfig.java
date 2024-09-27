package org.dailycodebuffer.codebufferspringbootmongodb.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class PostConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate(){
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }

    @Bean
    @Primary
    public PostService postService(){
        return new PostService(restTemplate());
    }

}
