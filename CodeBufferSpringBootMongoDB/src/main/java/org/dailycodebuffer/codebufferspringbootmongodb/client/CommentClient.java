package org.dailycodebuffer.codebufferspringbootmongodb.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Comment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CommentClient {

    private static final Log LOG = LogFactory.getLog(CommentClient.class);

    private RestClient restClient;

    public CommentClient(RestClient.Builder builder) {

        // if we use this then while testing using RestClientTest then there is no mocking of MockRestServiceServer
        // and it actually hits the real service and get the response in real
        //JdkClientHttpRequestFactory requestFactory=new JdkClientHttpRequestFactory();

        this.restClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
              //  .requestFactory(requestFactory)
                .build();
    }

    public List<Comment> getComments() {
        LOG.info("CommentClient.getComments called");
        return restClient.get()
                .uri("/comments")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Comment>>() {
                });
    }
}
