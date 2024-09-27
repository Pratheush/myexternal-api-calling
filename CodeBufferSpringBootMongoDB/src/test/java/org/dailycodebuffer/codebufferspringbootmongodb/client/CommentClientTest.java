package org.dailycodebuffer.codebufferspringbootmongodb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dailycodebuffer.codebufferspringbootmongodb.collection.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// this RestClientTest is used when service is down and we can mock the server or api service and test out.
// This RestClientTest is used when we use RestTemplate or RestClient

@RestClientTest(CommentClient.class)
@ExtendWith({MockitoExtension.class})
class CommentClientTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private CommentClient commentClient;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void getComments() throws JsonProcessingException {
        // given
        List<Comment> data=List.of(
                new Comment(1,1,"randeep","randeep@gmail.com","Wow Awesome Post Explaining The Details"),
                new Comment(1,1,"randeep","randeep@gmail.com","Wow Awesome Post Explaining The Details")
        );

        // when here you have to give full url i.e. baseUrl+uri
        server.expect(MockRestRequestMatchers.requestTo("https://jsonplaceholder.typicode.com/comments"))
                .andRespond(MockRestResponseCreators.withSuccess(mapper.writeValueAsString(data), MediaType.APPLICATION_JSON ));

        // then
        List<Comment> comments=commentClient.getComments();

        // assert
        assertNotNull(comments);
        assertEquals(2,comments.size());

    }
}